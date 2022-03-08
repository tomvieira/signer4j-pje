package br.jus.cnj.pje.office.core.imp;


import static br.jus.cnj.pje.office.core.IPjeOffice.ENVIRONMENT_VARIABLE;
import static com.github.utils4j.imp.Environment.resolveTo;

import br.jus.cnj.pje.office.IBootable;
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
  },
  FILEWATCH() {
    @Override
    public IPjeCommander<?, ?> create(IBootable boot) {
      return new PjeFileWatchServer(boot, resolveTo(ENVIRONMENT_VARIABLE, "watch").get());
    }
  }
}
