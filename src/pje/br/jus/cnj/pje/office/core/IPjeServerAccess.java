package br.jus.cnj.pje.office.core;

public interface IPjeServerAccess {

  String getId();
  
  String getApp();

  String getServer();

  String getCode();

  boolean isAutorized();
  
  IPjeServerAccess clone(boolean allowed);

  default IPjeServerAccess newInstance() {
    return clone(isAutorized());
  }
}
