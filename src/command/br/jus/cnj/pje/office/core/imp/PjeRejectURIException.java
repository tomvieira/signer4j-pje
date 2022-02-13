package br.jus.cnj.pje.office.core.imp;

public class PjeRejectURIException extends Exception {

  private static final long serialVersionUID = 2969433125674547900L;

  public PjeRejectURIException(String uri) {
    super("URI inválida ou já processada: " + uri);
  }
}
