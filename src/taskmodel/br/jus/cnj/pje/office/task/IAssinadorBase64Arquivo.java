package br.jus.cnj.pje.office.task;

import java.util.Optional;

import com.github.utils4j.IDisposable;

public interface IAssinadorBase64Arquivo extends IDisposable {

  Optional<String> getHashDoc();

  Optional<String> getConteudoBase64();
}