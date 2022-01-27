package br.jus.cnj.pje.office.core;

import com.github.signer4j.IFinishable;

import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;

public interface IPjeOffice extends IFinishable {
  
  void boot();

  void showCertificates();

  void showOfflineSigner();

  void showAuthorizedServers();

  void showActivities();

  void setDevMode();

  void setProductionMode();

  void setAuthStrategy(PjeAuthStrategy strategy);

  boolean isAwayStrategy();

  boolean isOneTimeStrategy();

  boolean isConfirmStrategy();

  void kill();
}