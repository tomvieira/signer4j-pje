package br.jus.cnj.pje.office.core.imp;

public class PJeClientException extends Exception {

  private static final long serialVersionUID = 4973079611496546423L;

  public PJeClientException(String message) {
    super(message);
  }
  
  public PJeClientException(Exception e) {
    super(e);
  }

  public PJeClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
