package br.jus.cnj.pje.office.core.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;
import br.jus.cnj.pje.office.core.IServerAccess;
import br.jus.cnj.pje.office.core.IServerAccessPermissionChecker;

class PjeServerAccessPersister implements IPjeServerAccessPersister {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeServerAccessPersister.class);
  
  private boolean loaded = false;
  
  private final IServerAccessPermissionChecker checker;
  
  private final Map<String, IServerAccess> pool = new HashMap<>();
  
  protected final IPjeConfigPersister persister;

  protected PjeServerAccessPersister(IServerAccessPermissionChecker checker, IPjeConfigPersister persister) {
    this.checker = Args.requireNonNull(checker, "checker is null");
    this.persister = Args.requireNonNull(persister, "persister is null");
  }
  
  protected boolean isLoaded() {
    return loaded;
  }

  @FunctionalInterface
  protected interface IPool {
    void add(IServerAccess access);
  }
  
  private void checkLoaded() {
    if (!isLoaded()) {
      load();
      this.loaded = true;
    }
  }
  
  @Override
  public IPjeServerAccessPersister reload() {
    this.loaded = false;
    this.pool.clear();
    return this;
  }
  
  @Override
  public final Optional<IServerAccess> hasPermission(String id){
    Args.requireNonNull(id, "id is null");
    checkLoaded();
    return Optional.ofNullable(pool.get(id));
  }

  @Override
  public final void save(IServerAccess access) throws PjePermissionDeniedException {
    Args.requireNonNull(access, "access is null");
    checkLoaded();
    checker.checkAccessPermission(access);
    access = access.newInstance();
    persist(access);
    pool.put(access.getId(), access);
  }

  @Override
  public final void remove(IServerAccess access) throws PjeTokenPersisterException {
    Args.requireNonNull(access, "access is null");
    checkLoaded();
    unpersist(access);
    pool.remove(access.getId());
  }
  
  protected void add(IServerAccess access) {
    if (access != null) {
      try {
        checker.checkAccessPermission(access); 
        pool.put(access.getId(), access); 
      } catch (PjePermissionDeniedException e) {
        unpersist(access);
        LOGGER.warn("Servidor de acesso não autorizado nas configurações (ignorado). ServerAccess: " + access.toString(), e);
      }
    }
  }
  
  protected void load() {
    persister.loadServerAccess(this::add);
  }
  
  protected void persist(IServerAccess... access) {
    persister.save(access);
  }

  protected void unpersist(IServerAccess access) {
    persister.delete(access);
  }
}
