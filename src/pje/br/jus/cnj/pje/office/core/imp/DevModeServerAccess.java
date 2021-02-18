package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IServerAccess;

public class DevModeServerAccess extends ServerAccessWrapper {

  protected DevModeServerAccess(IServerAccess access) {
    super(access);
  }
  
  @Override
  public boolean isAutorized() {
    return true;
  }

  @Override
  public IServerAccess clone(boolean allowed) {
    return new DevModeServerAccess(super.clone(allowed));
  }
}
