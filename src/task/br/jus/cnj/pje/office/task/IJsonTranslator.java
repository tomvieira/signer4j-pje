package br.jus.cnj.pje.office.task;

import com.github.utils4j.imp.Params;

public interface IJsonTranslator {

  String toJson(Params input) throws Exception;
}
