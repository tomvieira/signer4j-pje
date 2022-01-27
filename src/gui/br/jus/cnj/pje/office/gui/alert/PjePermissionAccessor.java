package br.jus.cnj.pje.office.gui.alert;

import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.imp.PjeAccessTime;

public class PjePermissionAccessor implements IPjePermissionAccessor {

  @Override
  public PjeAccessTime tryAccess(IPjeServerAccess access) {
    return PjeServerPermissionOptions.choose(access);
  }
}
