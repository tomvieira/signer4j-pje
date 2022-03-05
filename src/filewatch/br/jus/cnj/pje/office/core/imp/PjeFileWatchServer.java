package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.SimpleContext.of;
import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Directory;
import com.github.utils4j.imp.ThreadContext;
import com.github.utils4j.imp.Threads;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeContext;

class PjeFileWatchServer extends PjeURIServer {
  
  private final ShellPacker packer;

  public PjeFileWatchServer(IBootable boot, Path folderWatching) {
    super(boot, "filewatch://watch-service");
    this.packer = new ShellPacker(folderWatching);
  }

  @Override
  protected void doStart() throws IOException {
    packer.start();
    super.doStart();
  }
  
  @Override
  protected void doStop(boolean kill) {
    packer.stop();
    super.doStop(kill);
  }

  @Override
  protected void clearBuffer() {
    packer.clear();
  }

  @Override
  protected IPjeContext createContext(String input) throws Exception {
    return of(new PjeFileWatchRequest(input, boot.getOrigin()), new PjeFileWatchResponse());
  }

  @Override
  protected String getUri() throws InterruptedException, Exception {
    do {
      String r =  packer.getUri();
      System.out.println("Recebido para processamento: " + r);
      if (r == null) //WE HAVE GO TO BACK HERE!
        break;
    } while(true);
    return null;
  }

  private static class ShellPacker extends ThreadContext {
    
    private static final long TIMEOUT_TIMER = 5000;

    private final Path folderWatching;

    private WatchService watchService;

    private List<File> pathPool = new LinkedList<File>();

    private final List<String> uris =  new LinkedList<String>();
    
    private long startTime;
    
    public ShellPacker(Path folderWatching) {
      super("shell-packer");
      this.folderWatching = Args.requireFolderExists(folderWatching, "folder watching does not exists");
    }
    
    public void clear() {
      tryRun(() -> Directory.clean(folderWatching, (f) -> f.isFile()));
    }

    @Override
    protected void beforeRun() throws IOException {
      watchService = FileSystems.getDefault().newWatchService();
      folderWatching.register(watchService,  StandardWatchEventKinds.ENTRY_CREATE);
    }
    
    @Override
    protected void afterRun() {
      tryRun(watchService::close);
      clear();
      pathPool.forEach(File::delete);
      pathPool.clear();
      uris.clear();
    }
    
    private void pack(File file) {
      if (!file.isDirectory()) {
        pathPool.add(file);
        startTime = System.currentTimeMillis();
      }
    }

    public String getUri() throws InterruptedException {
      synchronized(uris) {
        while (uris.isEmpty()) {
          uris.wait();
        }
        return uris.remove(0);
      }
    }
    
    private List<File> block() {
      List<File> r = pathPool;
      pathPool = new LinkedList<File>();
      return r;
    }
    
    private boolean hasTimeout() {
      return System.currentTimeMillis() - startTime > TIMEOUT_TIMER;
    }

    @Override
    protected void doRun() {
      WatchKey key;
      try {
        do {
          while((key = watchService.poll(250, TimeUnit.MILLISECONDS)) == null) {
            if (!pathPool.isEmpty() && hasTimeout()) {
              buildURI(block());
            }
          }
          try {
            for (WatchEvent<?> e : key.pollEvents()) {
              if (e.count() <= 1) {
                @SuppressWarnings("unchecked")
                final WatchEvent<Path> event = (WatchEvent<Path>) e;
                final Path folder = (Path)key.watchable();
                final Path fileName = event.context();
                final Path file = folder.resolve(fileName);
                pack(file.toFile());
              }         
            }
           
            if (pathPool.isEmpty() || !hasTimeout()) {
              continue;
            }
            buildURI(block());
  
          }finally {
            key.reset();
          }
          
        } while(true);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    private void buildURI(List<File> block) throws InterruptedException {
      StringBuilder b = new StringBuilder("\n");
      int total = block.size();
      while(block.size() > 0) {
        File f = block.remove(0);
        b.append(f.getAbsolutePath()).append("\n");
        if (f.exists()) {
          f.delete();
          while(f.exists()) {
            Threads.sleep(250);
            f.delete();
          }
        }
      }
      b.append('\n').append("total: ").append(total);
      synchronized(uris) {
        uris.add(b.toString());
        uris.notifyAll();
      }
    }
  }
}

