package br.jus.cnj.pje.office.core;

import com.github.signer4j.imp.function.Runnable;

import br.jus.cnj.pje.office.core.imp.PJeClientException;

public interface IResultChecker extends Runnable<String, PJeClientException> {
  static IResultChecker NOTHING = (r) -> {};
}