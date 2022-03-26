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
