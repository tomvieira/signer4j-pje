package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Strings.trim;
import static com.github.signer4j.imp.Throwables.tryRun;

import java.io.IOException;

import com.github.signer4j.IFinishable;
import com.github.signer4j.IThreadContext;
import com.github.signer4j.imp.Ids;
import com.github.signer4j.imp.ThreadContext;

import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeTextServer extends PjeCommander<IPjeRequest, IPjeResponse> {

  private boolean started = false;

  private final IThreadContext capturer;
  
  public PjeTextServer(IFinishable finishingCode, String serverAddress) {
    super(finishingCode, serverAddress);
    this.capturer = new URICapturer(serverAddress);
  }
  
  @Override
  public synchronized void start() throws IOException {
    if (!isStarted()) {
      LOGGER.info("Iniciando PjeStdioServer");
      this.started = true;
      this.capturer.start();
      notifyStartup();
    }
  }

  @Override
  public synchronized boolean isStarted() {
    return started;
  }
  
  @Override
  public synchronized void stop(boolean kill) {
    if (isStarted()) {
      LOGGER.info("Parando PjeStdioServer");
      try {
        this.capturer.stop(2000);
      }finally {
        super.stop(kill);
        PjeTextServer.this.started = false;
      }
    }
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
      return PjeTextServer.this.createContext(uri);
    }
    
    @Override
    protected void doRun() {
      int errorCount = 0;
      do {
        final IPjeContext context;
        try {
          context = createContext();
          errorCount = 0;
        } catch (InterruptedException e) {
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
        new Thread(() -> submit(context)).start();
      }while(true);
    }
  }
  
  protected abstract String getUri() throws InterruptedException, Exception;
  
  protected abstract IPjeContext createContext(String uri) throws Exception ;
}
