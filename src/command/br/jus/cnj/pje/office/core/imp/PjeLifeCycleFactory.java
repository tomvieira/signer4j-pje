package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPJeLifeCycle;
import br.jus.cnj.pje.office.core.IPjeCommandFactory;

public enum PjeLifeCycleFactory implements IPjeCommandFactory {
  STDIO() {
    @Override
    public IPJeLifeCycle create(IBootable boot) {
      return new PjeStdioServer(boot);
    }
  },
  CLIP() {
    @Override
    public IPJeLifeCycle create(IBootable boot) {
      return new PjeClipServer(boot);
    }
  },
  WEB() {
    @Override
    public IPJeLifeCycle create(IBootable boot) {
      return new PjeWebServer(boot);
    }
  },
  FILEWATCH() {
    @Override
    public IPJeLifeCycle create(IBootable boot) {
      return new PjeFileWatchServer(boot);
    }
  }, 
  PRO {
    @Override
    public IPJeLifeCycle create(IBootable boot) {
      return new PJeCompositeLifeCycle(
        WEB.create(boot), 
        FILEWATCH.create(boot),
        CLIP.create(boot)
      );//, , STDIO.create(boot));
    }
  }
}
