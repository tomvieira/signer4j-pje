package br.jus.cnj.pje.office.core;

import java.util.Optional;

import br.jus.cnj.pje.office.core.imp.PjePermissionDeniedException;
import br.jus.cnj.pje.office.core.imp.PjeTokenPersisterException;

public interface IPjeServerAccessPersister {

  IPjeServerAccessPersister reload();
  
  Optional<IServerAccess> hasPermission(String id);

  void save(IServerAccess access) throws PjePermissionDeniedException;
  
  void remove(IServerAccess access) throws PjeTokenPersisterException;
  
  default void allow(IServerAccess access) throws PjePermissionDeniedException {
    save(access.clone(true));
  }
  
  default void disallow(IServerAccess access) throws PjePermissionDeniedException {
    save(access.clone(false));
  }

  void checkAccessPermission(IServerAccess serverRequest) throws PjePermissionDeniedException;
}
