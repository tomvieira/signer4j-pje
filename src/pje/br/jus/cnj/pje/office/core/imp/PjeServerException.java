package br.jus.cnj.pje.office.core.imp;

public class PjeServerException extends Exception {

  private static final long serialVersionUID = 4973079611496546423L;

  public PjeServerException(String message) {
    super(message);
  }
  
  public PjeServerException(Exception e) {
    super(e);
  }

  public PjeServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
