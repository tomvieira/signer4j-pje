package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;

public interface IPjeOffice {

  void boot();

  void showCertificates();

  void showOfflineSigner();

  void showAuthorizedServers();

  void showActivities();

  void setDevMode();

  void setProductionMode();

  void exit();

  void setAuthStrategy(PjeAuthStrategy strategy);

  boolean isAwayStrategy();

  boolean isOneTimeStrategy();

  boolean isConfirmStrategy();
}