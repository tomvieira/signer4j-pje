package br.jus.cnj.pje.office.core.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.github.signer4j.IFinishable;
import com.github.signer4j.imp.ThreadContext;

import br.jus.cnj.pje.office.web.IPjeRequest;
import br.jus.cnj.pje.office.web.IPjeResponse;

public class PjeStdioServer extends PjeCommander<IPjeRequest, IPjeResponse> {

  private boolean started = false;
  
  private final ThreadContext context;
  
  public PjeStdioServer(IFinishable finishingCode) {
    super(finishingCode);
    this.context = new StdioCycle();
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
      super.stop(kill);
      this.context.stop();
      notifyShutdown();
      if (kill) {
        notifyKill();
      }
    }
  }

  @Override
  protected void doShowOfflineSigner(String paramRequest) {
    System.out.println(paramRequest);
  }

  private class StdioCycle extends ThreadContext {

    public StdioCycle() {
      super("stdio");
    }

    @Override
    protected void doRun() {
      try(BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
        do {
          while (!bf.ready())
            try {
              Thread.sleep(200);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
          String input = bf.readLine();
          PjeStdioServer.this.execute(new PjeSysinRequest(input), new PjeSysoutResponse());
        }while(true);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    protected void doInterrupt() {
    }

    protected void onStoped() {
      PjeStdioServer.this.started = false;
    }
  }
}
