package br.jus.cnj.pje.office.web.imp;

import com.github.signer4j.IFinishable;

import br.jus.cnj.pje.office.web.IPjeWebServer;

public enum PJeWebServerFactory {
  DEFAULT;

  public IPjeWebServer create(IFinishable exitable) {
    return new PjeWebServer(exitable);
  }
}
