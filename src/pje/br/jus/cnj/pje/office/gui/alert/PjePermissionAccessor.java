package br.jus.cnj.pje.office.gui.alert;

import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IServerAccess;
import br.jus.cnj.pje.office.core.imp.PjeAccessTime;

public class PjePermissionAccessor implements IPjePermissionAccessor {

  @Override
  public PjeAccessTime tryAccess(IServerAccess access) {
    return PjeServerPermissionOptions.choose(access);
  }
}
