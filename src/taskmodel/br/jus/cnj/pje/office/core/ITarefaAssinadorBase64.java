package br.jus.cnj.pje.office.core;

import java.util.List;
import java.util.Optional;

public interface ITarefaAssinadorBase64 {
  
  Optional<String> getAlgoritmoAssinatura();

  Optional<String> getUploadUrl();

  List<IAssinadorBase64Arquivo> getArquivos();

  boolean isDeslogarKeyStore();

}
