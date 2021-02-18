package br.jus.cnj.pje.office.core;

import java.util.function.Supplier;

import br.jus.cnj.pje.office.signer4j.IPjeToken;

public interface IPjeTokenAccess extends Supplier<IPjeToken> {
  String TOKEN_ACCESS = IPjeTokenAccess.class.getSimpleName() + ".instance";
}
