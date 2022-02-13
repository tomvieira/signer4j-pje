package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Throwables.tryRun;

import java.io.IOException;

import com.github.signer4j.IFinishable;
import com.github.signer4j.imp.ThreadContext;

import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeTextServer extends PjeCommander<IPjeRequest, IPjeResponse> {

  private boolean started = false;

  private final ThreadContext context;
  
  public PjeTextServer(IFinishable finishingCode, String serverAddress) {
    super(finishingCode, serverAddress);
    this.context = new TextCycle(serverAddress);
  }
  
  @Override
  public synchronized void start() throws IOException {
    if (!isStarted()) {
      LOGGER.info("Iniciando PjeStdioServer");
      this.started = true;
      this.context.start();
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
        this.context.stop(2000);
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

  private class TextCycle extends ThreadContext {

    public TextCycle(String contextName) {
      super(contextName);
    }
    
    @Override
    protected void beforeRun() {
      clearBuffer();
    }

    @Override
    protected void doRun() {
      do {
        final IPjeContext context;
        try {
          context = createContext();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          LOGGER.warn("Thread interrompida", e);
          break;
        } catch (Exception e) {
          LOGGER.warn("Requisição mal formada", e);
          continue;
        }
        if (context == null) {
          LOGGER.info("Contexto não informado. Thread finalizada");
          break;
        }
        new Thread(() -> submit(context)).start();
      }while(true);      
    }
  }
  
  @Override
  protected final void openSigner(String request) {
   tryRun(() -> submit(createContext(getServerEndpoint("/") + request)));
  }
  
  protected void clearBuffer() {}

  protected abstract IPjeContext createContext(String input) throws Exception;
  
  protected abstract IPjeContext createContext() throws InterruptedException, Exception;
}
