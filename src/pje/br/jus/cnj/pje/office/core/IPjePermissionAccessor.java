package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.core.imp.PjeAccessTime;

public interface IPjePermissionAccessor {

  PjeAccessTime tryAccess(IServerAccess access);

}
