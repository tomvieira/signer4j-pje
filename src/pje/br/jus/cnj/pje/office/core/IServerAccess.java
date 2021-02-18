package br.jus.cnj.pje.office.core;

public interface IServerAccess {

  String getId();
  
  String getApp();

  String getServer();

  String getCode();

  boolean isAutorized();
  
  IServerAccess clone(boolean allowed);

  default IServerAccess newInstance() {
    return clone(isAutorized());
  }
}
