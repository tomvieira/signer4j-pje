package br.jus.cnj.pje.office.core.imp;

public class PjePermissionDeniedException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public PjePermissionDeniedException(String message) {
    super(message);
  }

  public PjePermissionDeniedException(String message, Exception cause) {
    super(message, cause);
  }
}
