package br.jus.cnj.pje.office.core.imp;

import java.util.Optional;

import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;

public enum PjeServerAccessPersisters implements IPjeServerAccessPersister {
  DEVMODE(new PjeServerAccessPersister(PjePermissionChecker.DEVMODE) {
    @Override
    protected void add(IPjeServerAccess access) {
      if (access != null) {
        super.add(new DevModeServerAccess(access));
      }
    }
  }),
  
  PRODUCTION(new PjeServerAccessPersister(PjePermissionChecker.PRODUCTION));
  
  public static void refresh() {
    for(PjeServerAccessPersisters p: PjeServerAccessPersisters.values()) {
      p.reload();
    }
  }
  
  private final IPjeServerAccessPersister persister;
  
  PjeServerAccessPersisters(IPjeServerAccessPersister persister) {
    this.persister = persister;
  }

  @Override
  public Optional<IPjeServerAccess> hasPermission(String id) {
    return persister.hasPermission(id);
  }

  @Override
  public void save(IPjeServerAccess access) throws PjePermissionDeniedException {
    persister.save(access);
  }

  @Override
  public void remove(IPjeServerAccess access) throws PjeTokenPersisterException {
    persister.remove(access);
  }

  @Override
  public IPjeServerAccessPersister reload() {
    persister.reload();
    return this;
  }

  @Override
  public void checkAccessPermission(IPjeServerAccess serverRequest) throws PjePermissionDeniedException {
    persister.checkAccessPermission(serverRequest);
  }
}
