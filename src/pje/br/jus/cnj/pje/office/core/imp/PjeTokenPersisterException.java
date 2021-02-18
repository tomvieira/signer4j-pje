package br.jus.cnj.pje.office.core.imp;

public class PjeTokenPersisterException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public PjeTokenPersisterException(String message) {
    super(message);
  }
  
  public PjeTokenPersisterException(String message, Throwable cause) {
    super(message, cause);
  }
}
