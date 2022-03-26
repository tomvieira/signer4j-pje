/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.core.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.IPjeServerAccessPermissionChecker;
import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;

class PjeServerAccessPersister implements IPjeServerAccessPersister {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeServerAccessPersister.class);
  
  private boolean loaded = false;
  
  private final IPjeServerAccessPermissionChecker checker;
  
  private final Map<String, IPjeServerAccess> pool = new HashMap<>();
  
  protected PjeServerAccessPersister(IPjeServerAccessPermissionChecker checker) {
    this.checker = Args.requireNonNull(checker, "checker is null");
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
    PjeConfig.loadServerAccess(this::add);
  }
  
  protected void persist(IPjeServerAccess... access) {
    PjeConfig.save(access);
  }

  protected void unpersist(IPjeServerAccess access) {
    PjeConfig.delete(access);
  }
}
