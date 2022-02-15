package br.jus.cnj.pje.office.core.imp;

import com.github.signer4j.IBootable;

import br.jus.cnj.pje.office.core.IPjeCommandFactory;
import br.jus.cnj.pje.office.core.IPjeCommander;

public enum PjeCommandFactory implements IPjeCommandFactory {
  STDIO() {
    @Override
    public IPjeCommander<?, ?> create(IBootable boot) {
      return new PjeStdioServer(boot);
    }
  },
  CLIP() {
    @Override
    public IPjeCommander<?, ?> create(IBootable boot) {
      return new PjeClipServer(boot);
    }
  },
  WEB() {
    @Override
    public IPjeCommander<?, ?> create(IBootable boot) {
      return new PjeWebServer(boot);
    }
  }
}
