package br.jus.cnj.pje.office.core.imp;


import static br.jus.cnj.pje.office.core.IPjeOffice.ENVIRONMENT_VARIABLE;
import static com.github.utils4j.imp.Environment.resolveTo;

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
      return new PjeFileWatchServer(boot, resolveTo(ENVIRONMENT_VARIABLE, "watch").orElseThrow(
        () -> new IllegalArgumentException("Não encontrada variável de ambiente PJEOFFICE_HOME"))
      );
    }
  }, 
  PRO {
    @Override
    public IPJeLifeCycle create(IBootable boot) {
      return new PJeCompositeLifeCycle(
        WEB.create(boot), 
        FILEWATCH.create(boot)
      );//, CLIP.create(boot), STDIO.create(boot));
    }
  }
}
