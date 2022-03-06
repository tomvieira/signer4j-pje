package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.SimpleContext.of;
import static com.github.utils4j.IConstants.UTF_8;
import static com.github.utils4j.imp.Strings.empty;
import static com.github.utils4j.imp.Throwables.tryCall;
import static com.github.utils4j.imp.Throwables.tryRuntime;
import static java.nio.file.Files.readAllBytes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.utils4j.IFilePacker;
import com.github.utils4j.imp.FilePacker;
import com.github.utils4j.imp.Pair;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.task.imp.PjeTaskReader;

class PjeFileWatchServer extends PjeURIServer {
  
  private final IFilePacker packer;
  
  private final Map<PjeTaskReader, List<String>> blockPerTask = new HashMap<>();

  public PjeFileWatchServer(IBootable boot, Path folderWatching) {
    super(boot, "filewatch://watch-service");
    this.packer = new FilePacker(folderWatching);
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
    blockPerTask.clear();
    packer.reset();
  }

  @Override
  protected IPjeContext createContext(String input) throws Exception {
    return of(new PjeFileWatchRequest(input, boot.getOrigin()), new PjeFileWatchResponse());
  }
  
  private Optional<String> nextUri() throws Exception {
    Optional<PjeTaskReader> tr = blockPerTask.keySet().stream().findFirst();
    if (!tr.isPresent()) {
      return Optional.empty();
    }
    PjeTaskReader r = tr.get();
    List<String> list = blockPerTask.get(r);
    try {
      Params params = Params.create()
          .of("servidor", getServerEndpoint())
          .of("arquivos", list);
      return Optional.of(getServerEndpoint(r.toUri(params)));
    }finally {
      blockPerTask.remove(r);
      list.clear();
    }
  }
  
  @Override
  protected String getUri() throws InterruptedException, Exception {
    try {
      return nextUri().orElseGet(() -> {
        Optional<String> uri = Optional.empty();
        do {
          List<File> block =  tryRuntime(() -> packer.filePackage());
    
          final PjeTaskReader[] readers = PjeTaskReader.values();
          
          block.stream()
            .map(f -> Pair.of(f, new File(tryCall(() -> new String(readAllBytes(f.toPath()), UTF_8), empty()))))
            .forEach(p -> {
              File key = p.getKey();
              final String keyName = key.getName();
              key.delete();
              File value = p.getValue();
              if (!value.exists()) {
                return;
              }
              Optional<PjeTaskReader> tr = Stream.of(readers).filter(r -> keyName.startsWith(r.getId())).findFirst();
              if (!tr.isPresent()) {
                return;
              }
              PjeTaskReader r = tr.get();
              List<String> list = blockPerTask.get(r);
              if (list == null)
                blockPerTask.put(r, list = new ArrayList<>());
              list.add(value.getAbsolutePath());
            });
          
          uri = tryRuntime(PjeFileWatchServer.this::nextUri);
          
        }while(!uri.isPresent());
        
        return uri.get();
      });
    }catch(RuntimeException e) {
      Throwable cause = e.getCause();
      throw Exception.class.isInstance(cause) ? (Exception)cause : e;
    }
  }
}

