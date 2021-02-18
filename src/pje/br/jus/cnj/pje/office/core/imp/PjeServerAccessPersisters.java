package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PJeConfigPersister.CONF;

import java.util.Optional;

import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;
import br.jus.cnj.pje.office.core.IServerAccess;

public enum PjeServerAccessPersisters implements IPjeServerAccessPersister {
  DEVMODE(new PjeServerAccessPersister(PjePermissionChecker.DEVMODE, CONF) {
    @Override
    protected void add(IServerAccess access) {
      if (access != null) {
        super.add(new DevModeServerAccess(access));
      }
    }
  }),
  
  PRODUCTION(new PjeServerAccessPersister(PjePermissionChecker.PRODUCTION, CONF));
  
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
  public Optional<IServerAccess> hasPermission(String id) {
    return persister.hasPermission(id);
  }

  @Override
  public void save(IServerAccess access) throws PjePermissionDeniedException {
    persister.save(access);
  }

  @Override
  public void remove(IServerAccess access) throws PjeTokenPersisterException {
    persister.remove(access);
  }

  @Override
  public IPjeServerAccessPersister reload() {
    persister.reload();
    return this;
  }
}
