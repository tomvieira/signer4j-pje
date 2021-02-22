package br.jus.cnj.pje.office.core;

import java.util.Optional;

import br.jus.cnj.pje.office.core.imp.PjePermissionDeniedException;
import br.jus.cnj.pje.office.core.imp.PjeTokenPersisterException;

public interface IPjeServerAccessPersister {

  IPjeServerAccessPersister reload();
  
  Optional<IPjeServerAccess> hasPermission(String id);

  void save(IPjeServerAccess access) throws PjePermissionDeniedException;
  
  void remove(IPjeServerAccess access) throws PjeTokenPersisterException;
  
  default void allow(IPjeServerAccess access) throws PjePermissionDeniedException {
    save(access.clone(true));
  }
  
  default void disallow(IPjeServerAccess access) throws PjePermissionDeniedException {
    save(access.clone(false));
  }

  void checkAccessPermission(IPjeServerAccess serverRequest) throws PjePermissionDeniedException;
}
