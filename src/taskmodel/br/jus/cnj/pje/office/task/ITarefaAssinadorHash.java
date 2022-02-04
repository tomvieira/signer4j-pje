package br.jus.cnj.pje.office.task;

import java.util.List;
import java.util.Optional;

public interface ITarefaAssinadorHash {

  boolean isModoTeste();

  @Deprecated
  boolean isDeslogarKeyStore();

  Optional<String> getAlgoritmoAssinatura();

  Optional<String> getUploadUrl();

  List<IAssinadorHashArquivo> getArquivos();
}