package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.core.imp.PjePermissionDeniedException;

public interface IPjeServerAccessPermissionChecker {
  
  void checkAccessPermission(IPjeServerAccess access) throws PjePermissionDeniedException;

}
