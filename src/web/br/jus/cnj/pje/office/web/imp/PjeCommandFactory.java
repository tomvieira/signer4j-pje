package br.jus.cnj.pje.office.web.imp;

import com.github.signer4j.IFinishable;

import br.jus.cnj.pje.office.core.IPjeCommandFactory;
import br.jus.cnj.pje.office.core.IPjeCommander;
import br.jus.cnj.pje.office.core.imp.PjeClipServer;
import br.jus.cnj.pje.office.core.imp.PjeStdioServer;

public enum PjeCommandFactory implements IPjeCommandFactory {
  STDIO() {
    @Override
    public IPjeCommander<?, ?> create(IFinishable finishingCode) {
      return new PjeStdioServer(finishingCode);
    }
  },
  CLIP() {
    @Override
    public IPjeCommander<?, ?> create(IFinishable finishingCode) {
      return new PjeClipServer(finishingCode);
    }
  },
  WEB() {
    @Override
    public IPjeCommander<?, ?> create(IFinishable finishingCode) {
      return new PjeWebServer(finishingCode);
    }
  }
}
