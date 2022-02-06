package br.jus.cnj.pje.office.web.imp;

import com.github.signer4j.IFinishable;

import br.jus.cnj.pje.office.core.IPjeCommander;

public enum PjeCommandFactory {
  DEFAULT;

  public IPjeCommander<?, ?> create(IFinishable exitable) {
    return new PjeWebServer(exitable);
  }
}
