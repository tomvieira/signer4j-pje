package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.signer4j.IFinishable;

import br.jus.cnj.pje.office.task.imp.PjeTaskRequestExecutor;
import br.jus.cnj.pje.office.web.IPjeRequest;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeBrowserExtension extends PjeCommander<IPjeRequest, IPjeResponse> {

  protected PjeBrowserExtension(PjeTaskRequestExecutor executor, IFinishable finishingCode) {
    super(executor, finishingCode);
  }

  @Override
  public void start() throws IOException {
    // TODO we have to go back here!
  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  protected void doShowOfflineSigner(String paramRequest) {
    // TODO we have to go back here!
  }
}
