package br.jus.cnj.pje.office.gui.servetlist;

import java.util.List;

interface IPjeServerList {
  
  enum Authorization {
    SIM, NÃO;

    static Authorization from(boolean autorized) {
      return autorized ? SIM : NÃO;
    }
  }
  
  public static enum Action {
    REMOVER;
    @Override
    public String toString() {
      return "Remover";
    }
  }
  
  public static interface IServerEntry {
    String getApp();
    String getServer();
    Authorization getAuthorization();
    String getCode();
    Action getAction();
    IServerEntry clone();
    void setAuthorization(Authorization aValue);
  }
  
  public List<IServerEntry> show(List<IServerEntry> entries);
}