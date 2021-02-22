package br.jus.cnj.pje.office.core.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeConfigPersister;
import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.IPjeServerAccessPermissionChecker;

class PjeServerAccessPersister implements IPjeServerAccessPersister {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeServerAccessPersister.class);
  
  private boolean loaded = false;
  
  private final IPjeServerAccessPermissionChecker checker;
  
  private final Map<String, IPjeServerAccess> pool = new HashMap<>();
  
  protected final IPjeConfigPersister persister;

  protected PjeServerAccessPersister(IPjeServerAccessPermissionChecker checker, IPjeConfigPersister persister) {
    this.checker = Args.requireNonNull(checker, "checker is null");
    this.persister = Args.requireNonNull(persister, "persister is null");
  }
  
  protected boolean isLoaded() {
    return loaded;
  }

  @FunctionalInterface
  protected interface IPool {
    void add(IPjeServerAccess access);
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
  public final Optional<IPjeServerAccess> hasPermission(String id){
    Args.requireNonNull(id, "id is null");
    checkLoaded();
    return Optional.ofNullable(pool.get(id));
  }
  
  @Override
  public void checkAccessPermission(IPjeServerAccess access) throws PjePermissionDeniedException {
    checker.checkAccessPermission(access);
  }
  
  @Override
  public final void save(IPjeServerAccess access) throws PjePermissionDeniedException {
    Args.requireNonNull(access, "access is null");
    checkLoaded();
    checkAccessPermission(access);
    access = access.newInstance();
    persist(access);
    pool.put(access.getId(), access);
  }

  @Override
  public final void remove(IPjeServerAccess access) throws PjeTokenPersisterException {
    Args.requireNonNull(access, "access is null");
    checkLoaded();
    unpersist(access);
    pool.remove(access.getId());
  }
  
  protected void add(IPjeServerAccess access) {
    if (access != null) {
      try {
        checkAccessPermission(access); 
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
  
  protected void persist(IPjeServerAccess... access) {
    persister.save(access);
  }

  protected void unpersist(IPjeServerAccess access) {
    persister.delete(access);
  }
}
