package br.jus.cnj.pje.office.core;


import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;

public interface IPostCodec<T> {
  PjeTaskResponse post(Supplier<T> supplier, IResultChecker checker) throws Exception ;
}
