package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Throwables.tryCall;
import static com.github.utils4j.imp.Throwables.tryRuntime;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.utils4j.IConstants;
import com.github.utils4j.IFilePacker;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.FilePacker;
import com.github.utils4j.imp.Pair;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.imp.PjeTaskReader;

class PjeFileWatchServer extends PjeURIServer {
  
  private final IFilePacker<IOException> packer;
  
  private final Map<PjeTaskReader, List<String[]>> blockPerTask = new HashMap<>();

  public PjeFileWatchServer(IBootable boot, Path folderWatching) {
    super(boot, "filewatch://watch-service");
    this.packer = new FilePacker<IOException>(folderWatching);
  }

  @Override
  protected void doStart() throws IOException {
    packer.start();
    super.doStart();
  }
  
  @Override
  protected void doStop(boolean kill) throws IOException {
    packer.stop();
    super.doStop(kill);
  }

  @Override
  protected void clearBuffer() {
    blockPerTask.clear();
    packer.reset();
  }

  @Override
  protected IPjeResponse createResponse() throws Exception {
    return new PjeFileWatchResponse();
  }

  @Override
  protected IPjeRequest createRequest(String uri, String origin) throws Exception {
    return new PjeFileWatchRequest(uri, origin);
  }
  
  private Optional<String> nextUri()  {
    do {
      Optional<PjeTaskReader> tr = blockPerTask.keySet().stream().findFirst();
      if (!tr.isPresent()) {
        return Optional.empty();
      }
      PjeTaskReader r = tr.get();
      List<String[]> arguments = blockPerTask.get(r);
      try {
        Params params = Params.create()
            .of("servidor", getServerEndpoint())
            .of(Params.DEFAULT_KEY, arguments);
        return Optional.of(getServerEndpoint(r.toUri(params)));
      } catch(Exception e) {
        LOGGER.warn("URI mal formada em fileWatchServer.nextUri() - reader: " + r.getId() + ". Tarefa ignorada/escapada", e);
      } finally {
        blockPerTask.remove(r);
        arguments.clear();
      }
    }while(true);
  }
  
  @Override
  protected String getUri() throws InterruptedException, Exception {
    try {
      return nextUri().orElseGet(() -> {
        Optional<String> uri = Optional.empty();
        do {
          List<File> block =  tryRuntime(() -> packer.filePackage());
          
          PjeTaskReader[] readers = PjeTaskReader.values();
          
          block.stream()
            .map(f -> Pair.of(f, tryCall(() -> Files.readAllLines(f.toPath(), IConstants.UTF_16LE), Collections.<String>emptyList())))
            .forEach(p -> {
              File key = p.getKey();
              final String keyName = key.getName().toLowerCase();
              
              Optional<PjeTaskReader> tr = Stream.of(readers).filter(r -> keyName.startsWith(r.getId())).findFirst();
              if (!tr.isPresent()) {
                return;
              }

              key.delete(); //this is very important!
              List<String> value = p.getValue().stream()
                .map(Strings::trim)
                .filter(Strings::hasText)
                .collect(toList());
              
              if (Containers.isEmpty(value)) {
                return;
              }
              
              File input = new File(value.get(0));
              if (!input.exists()) {
                return;
              }
              
              PjeTaskReader r = tr.get();
              if (!r.accept(input)) {
                return;
              }
              List<String[]> list = blockPerTask.get(r);
              if (list == null)
                blockPerTask.put(r, list = new ArrayList<>());
              list.add(value.toArray(new String[value.size()]));
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

