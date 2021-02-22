package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IPjeServerAccess;

public class PJeServerAccessWrapper implements IPjeServerAccess {

  private IPjeServerAccess access;
  
  protected PJeServerAccessWrapper(IPjeServerAccess access) {
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
  public IPjeServerAccess clone(boolean allowed) {
    return access.clone(allowed);
  }

}
