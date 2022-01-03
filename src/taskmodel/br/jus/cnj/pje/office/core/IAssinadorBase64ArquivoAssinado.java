package br.jus.cnj.pje.office.core;

import com.github.signer4j.IDisposable;

public interface IAssinadorBase64ArquivoAssinado extends IDisposable{

  String getHashDoc();

  String getAssinaturaBase64();

}