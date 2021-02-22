package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IPjeServerAccess;

public class DevModeServerAccess extends PJeServerAccessWrapper {

  protected DevModeServerAccess(IPjeServerAccess access) {
    super(access);
  }
  
  @Override
  public boolean isAutorized() {
    return true;
  }

  @Override
  public IPjeServerAccess clone(boolean allowed) {
    return new DevModeServerAccess(super.clone(allowed));
  }
}
