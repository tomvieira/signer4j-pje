package br.jus.cnj.pje.office.task;

import java.util.Optional;

public interface IAssinadorHashArquivo {

  Optional<String> getId();

  Optional<String> getCodIni();

  Optional<String> getHash();

  Optional<Long> getIdTarefa();

  Optional<Boolean> getIsBin(); //TODO não é usado em lugar algum?
}