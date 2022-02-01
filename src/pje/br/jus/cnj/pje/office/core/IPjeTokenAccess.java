package br.jus.cnj.pje.office.core;

import java.util.function.Supplier;

import br.jus.cnj.pje.office.signer4j.IPjeToken;

public interface IPjeTokenAccess extends Supplier<IPjeToken> {
  String PARAM_NAME = IPjeTokenAccess.class.getSimpleName() + ".instance";

  void logout();
}
