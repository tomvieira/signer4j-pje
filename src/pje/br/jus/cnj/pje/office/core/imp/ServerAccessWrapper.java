package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IServerAccess;

public class ServerAccessWrapper implements IServerAccess {

  private IServerAccess access;
  
  protected ServerAccessWrapper(IServerAccess access) {
    this.access = access;
  }

  @Override
  public String getId() {
    return access.getId();
  }

  @Override
  public String getApp() {
    return access.getApp();
  }

  @Override
  public String getServer() {
    return access.getServer();
  }

  @Override
  public String getCode() {
    return access.getCode();
  }

  @Override
  public boolean isAutorized() {
    return access.isAutorized();
  }

  @Override
  public IServerAccess clone(boolean allowed) {
    return access.clone(allowed);
  }

}
