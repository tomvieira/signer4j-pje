package br.jus.cnj.pje.office.core.imp;

public class PjeServerAccessPermissionException extends PjePermissionDeniedException {
  private static final long serialVersionUID = 1L;
  
  public PjeServerAccessPermissionException(String message) {
    super(message);
  }

  public PjeServerAccessPermissionException(String message, Exception cause) {
    super(message, cause);
  }
}
