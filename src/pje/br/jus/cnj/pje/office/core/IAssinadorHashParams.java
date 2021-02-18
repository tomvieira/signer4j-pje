package br.jus.cnj.pje.office.core;

import java.util.List;
import java.util.Optional;

public interface IAssinadorHashParams {

  String PJE_TAREFA_ASSINADOR_HASH = IAssinadorHashParams.class.getSimpleName();
  
  boolean isModoTeste();

  boolean isDeslogarKeyStore();

  Optional<String> getAlgoritmoAssinatura();

  Optional<String> getUploadUrl();

  List<IAssinadorHashArquivo> getArquivos();
}