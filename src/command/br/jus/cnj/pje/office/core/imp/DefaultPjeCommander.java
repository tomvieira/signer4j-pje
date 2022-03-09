package br.jus.cnj.pje.office.core.imp;

import com.github.progress4j.IProgressFactory;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;

abstract class DefaultPjeCommander extends AbstractPjeCommander <IPjeRequest, IPjeResponse> {

  protected DefaultPjeCommander(IBootable boot, String serverEndpoint) {
    super(boot, serverEndpoint);
  }
  
  protected DefaultPjeCommander(IBootable boot, String serverEndpoint, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent) {
    super(boot, serverEndpoint, tokenAccess, securityAgent);
  }

  protected DefaultPjeCommander(IBootable boot, String serverEndpoint, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent, IProgressFactory factory) {
    super(boot, serverEndpoint, tokenAccess, securityAgent, factory);
  }
}
