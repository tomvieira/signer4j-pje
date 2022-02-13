package br.jus.cnj.pje.office.core;

import com.github.signer4j.imp.function.Runnable;

public interface IResultChecker extends Runnable<String, Exception> {
  static IResultChecker NOTHING = (r) -> {};
}