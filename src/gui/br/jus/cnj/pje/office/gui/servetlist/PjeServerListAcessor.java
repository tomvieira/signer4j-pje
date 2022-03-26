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


package br.jus.cnj.pje.office.gui.servetlist;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.imp.PjeConfig;
import br.jus.cnj.pje.office.core.imp.PjeServerAccess;
import br.jus.cnj.pje.office.core.imp.PjeServerAccessPersisters;
import br.jus.cnj.pje.office.gui.servetlist.IPjeServerListUI.Action;
import br.jus.cnj.pje.office.gui.servetlist.IPjeServerListUI.Authorization;
import br.jus.cnj.pje.office.gui.servetlist.IPjeServerListUI.IServerEntry;

public enum PjeServerListAcessor implements IPjeServerListAcessor {
  INSTANCE;
  
  private static class ServerEntry implements IServerEntry {
    private IPjeServerAccess serverAccess;
    private Authorization authorization;
    
    private ServerEntry(IPjeServerAccess serverAccess) {
      this.serverAccess = Args.requireNonNull(serverAccess, "serverAccess is null");
      this.authorization = Authorization.from(serverAccess.isAutorized());
    }
    
    @Override
    public String getApp() {
      return serverAccess.getApp();
    }

    @Override
    public String getServer() {
      return serverAccess.getServer();
    }

    @Override
    public Authorization getAuthorization() {
      return this.authorization;
    }

    @Override
    public Action getAction() {
      return Action.REMOVER;
    }

    @Override
    public String getCode() {
      return serverAccess.getCode();
    }
    
    @Override
    public IServerEntry clone() {
      return new ServerEntry(serverAccess.newInstance());
    }
    
    @Override
    public void setAuthorization(Authorization authorization) {
      this.authorization = authorization;
    }
  }
  
  private final IPjeServerListUI serverList = new PjeServerListUI();
  
  @Override
  public void show() {
    List<IServerEntry> entries = new ArrayList<>();
    PjeConfig.loadServerAccess(sa -> entries.add(new ServerEntry(sa)));
    List<IPjeServerAccess> serverAccess = serverList
      .show(entries)
      .stream()
      .map(se -> new PjeServerAccess(
        se.getApp(), 
        se.getServer(), 
        se.getCode(), 
        Authorization.SIM.equals(se.getAuthorization())))
      .collect(toList());
    PjeConfig.overwrite(serverAccess.toArray(new IPjeServerAccess[serverAccess.size()]));
    PjeServerAccessPersisters.refresh();
  }
}
