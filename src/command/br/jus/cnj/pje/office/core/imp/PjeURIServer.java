package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Strings.trim;
import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.io.InterruptedIOException;

import com.github.utils4j.IThreadContext;
import com.github.utils4j.imp.Ids;
import com.github.utils4j.imp.ThreadContext;
import com.github.utils4j.imp.Threads;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeURIServer extends PjeCommander<IPjeRequest, IPjeResponse> {

  private final IThreadContext capturer;

  public PjeURIServer(IBootable boot, String serverAddress) {
    super(boot, serverAddress);
    this.capturer = new URICapturer(serverAddress);
  }

  @Override
  protected void doStart() throws IOException {
    this.capturer.start();
    super.doStart();
  }

  @Override
  protected void doStop(boolean kill) {
    this.capturer.stop(2000);
    super.doStop(kill);
  }

  protected void submit(IPjeContext context) {
    if (context != null) {
      executor.setAllowLocalRequest(true);
      try {
        super.execute(context.getRequest(), context.getResponse());
      } finally {
        executor.setAllowLocalRequest(false);
      }
    }
  }

  protected void clearBuffer() {}

  @Override
  protected final void openSigner(String request) {
    tryRun(() -> submit(createContext(getServerEndpoint("/") + request)));
  }

  private class URICapturer extends ThreadContext {

    private String lastUri = Ids.next();

    public URICapturer(String contextName) {
      super(contextName);
    }

    @Override
    protected void beforeRun() {
      clearBuffer();
    }

    protected boolean isLast(String uri) {
      return lastUri.equals(uri);
    }

    protected boolean isValid(String uri) {
      return !isLast(uri) && uri.startsWith(getServerEndpoint());
    }

    private final IPjeContext createContext() throws Exception {
      String uri = trim(getUri());
      if (!isValid(uri)) {
        lastUri = uri;
        return null;
      }
      lastUri = uri;
      return PjeURIServer.this.createContext(uri);
    }

    @Override
    protected void doRun() {
      int errorCount = 0;
      do {
        final IPjeContext context;
        try {
          context = createContext();
          errorCount = 0;
        } catch (InterruptedException | InterruptedIOException e) {
          Thread.currentThread().interrupt();
          LOGGER.warn("Thread interrompida", e);
          break;
        } catch (Exception e) {
          LOGGER.warn("Requisição mal formada", e);
          if (++errorCount > 10) {
            LOGGER.error("Total máximo de erros alcançado: {}. Thread finalizada", errorCount);
            break;
          }
          if (!Thread.interrupted()) {
            clearBuffer();
            continue;
          }
          break;
        }
        if (context == null) {
          LOGGER.info("Contexto indisponível");
          continue;
        }
        Threads.startAsync("Tratando contexto: " + context.getId(), () -> submit(context));
      }while(true);
    }
  }

  protected abstract String getUri() throws InterruptedException, Exception;

  protected abstract IPjeContext createContext(String uri) throws Exception ;
}
