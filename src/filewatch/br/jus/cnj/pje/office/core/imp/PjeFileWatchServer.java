package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Strings.textOrNull;
import static com.github.utils4j.imp.Strings.trim;
import static com.github.utils4j.imp.Throwables.tryRun;
import static com.github.utils4j.imp.Throwables.tryRuntime;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.github.utils4j.IFilePacker;
import com.github.utils4j.imp.FilePacker;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.function.Executable;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.imp.PjeTaskReader;

class PjeFileWatchServer extends PjeURIServer {
  
  private final IFilePacker<IOException> packer;
  
  private final Map<PjeTaskReader, List<Properties>> blockPerTask = new HashMap<>();

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
      List<Properties> block = blockPerTask.get(r);

      try {
        final Params params = Params.create();

        List<String> arquivos = block.stream()
          .peek(p -> p.forEach((k, v) -> params.of(trim(k), textOrNull(trim(v)))))
          .map (p -> p.getProperty("arquivo"))
          .collect(toList());
      
        params.of("servidor", getServerEndpoint())
              .of("arquivos", arquivos);
        return Optional.of(getServerEndpoint(r.toUri(params)));
      } catch(Exception e) {
        LOGGER.warn("URI mal formada em fileWatchServer.nextUri() - reader: " + r.getId() + ". Tarefa ignorada/escapada", e);
      } finally {
        blockPerTask.remove(r);
        block.clear();
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

          block.forEach(f -> {
            final String fileName = f.getName();
            final Properties p = new Properties();
            final Executable<?> loadProperties = () -> {
              try(FileInputStream stream = new FileInputStream(f)) {
                p.load(stream);
              } catch (Exception e) {
                LOGGER.error("Não foi possível ler o arquivo '" + fileName + "'. Arquivo ignorado! (acesso simultâneo?)", e);
                throw e;
              } finally {
                f.delete(); //this is very important!
              }
            };
            if (!tryRun(loadProperties, false, true)) {
              return;
            }
            
            Optional<PjeTaskReader> tr = PjeTaskReader.task(p.getProperty("task", ""));
            if (!tr.isPresent()) {
              LOGGER.warn("Aquivo '" + fileName + "' com campo 'task' vazio. Arquivo ignorado!");
              return;
            }
            
            File input = new File(p.getProperty("arquivo", ""));
            if (!input.exists() || input.length() == 0) {
              LOGGER.warn("Aquivo '" + fileName + "' com campo 'arquivo' vazio ou de tamanho zero. Arquivo ignorado!");
              return;
            }
            
            PjeTaskReader r = tr.get();
            if (!r.accept(input)) {
              LOGGER.warn("Tarefa '" + r.getId() + "' não aceita arquivo '" + input.getName() + "'. Tarefa ignorada!");
              return;
            }
            
            List<Properties> list = blockPerTask.get(r);
            if (list == null)
              blockPerTask.put(r, list = new ArrayList<>());
            list.add(p);
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

