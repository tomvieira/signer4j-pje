package br.jus.cnj.pje.office.task;

import com.github.utils4j.IDisposable;

public interface IAssinadorBase64ArquivoAssinado extends IDisposable{

  String getHashDoc();

  String getAssinaturaBase64();

}