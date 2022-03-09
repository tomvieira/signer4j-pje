package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;

public interface IPjeOffice extends IBootable {
  
  public static final String ENVIRONMENT_VARIABLE = "PJEOFFICE_HOME";
  
  void showCertificates();

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