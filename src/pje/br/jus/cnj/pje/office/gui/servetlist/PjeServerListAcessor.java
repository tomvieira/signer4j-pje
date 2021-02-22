package br.jus.cnj.pje.office.gui.servetlist;

import static br.jus.cnj.pje.office.core.imp.PJeConfigPersister.CONF;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeServerAccess;
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
    CONF.loadServerAccess(sa -> entries.add(new ServerEntry(sa)));
    List<IPjeServerAccess> serverAccess = serverList
      .show(entries)
      .stream()
      .map(se -> new PjeServerAccess(
        se.getApp(), 
        se.getServer(), 
        se.getCode(), 
        Authorization.SIM.equals(se.getAuthorization())))
      .collect(toList());
    CONF.overwrite(serverAccess.toArray(new IPjeServerAccess[serverAccess.size()]));
    PjeServerAccessPersisters.refresh();
  }
}
