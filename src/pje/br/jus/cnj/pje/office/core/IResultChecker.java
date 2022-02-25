package br.jus.cnj.pje.office.core;

import com.github.utils4j.imp.function.Runnable;

public interface IResultChecker extends Runnable<String, Exception> {
  static IResultChecker NOTHING = (r) -> {};
}