package br.jus.cnj.pje.office.core.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import com.github.signer4j.IFinishable;
import com.github.signer4j.imp.ThreadContext;
import com.github.signer4j.imp.Throwables;

import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

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
  protected void openSigner(String request) {
    final String r = "native://messaging/?" + 
      "r=" + request + "&" +
      "u=" + System.currentTimeMillis();
    Throwables.tryRun(() -> submit(r));
  }

  private void submit(String input) throws URISyntaxException {
    executor.setAllowLocalRequest(true);
    try {
      super.execute(new PjeSysinRequest(input), new PjeSysoutResponse());
    } finally {
      executor.setAllowLocalRequest(false);
    }
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
          submit(bf.readLine());
        }while(true);
      } catch (IOException | URISyntaxException e) {
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
