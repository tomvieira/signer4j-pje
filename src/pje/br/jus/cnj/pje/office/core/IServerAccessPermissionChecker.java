package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.core.imp.PjePermissionDeniedException;

public interface IServerAccessPermissionChecker {
  
  void checkAccessPermission(IServerAccess access) throws PjePermissionDeniedException;

}
