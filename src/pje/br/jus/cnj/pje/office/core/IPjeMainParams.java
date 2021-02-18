package br.jus.cnj.pje.office.core;

import java.util.Optional;

public interface IPjeMainParams {

  String PJE_MAIN_REQUEST_PARAM = IPjeMainParams.class.getSimpleName() + ".instance";
  
  Optional<String> getServidor();

  Optional<String> getAplicacao();

  Optional<String> getSessao();

  Optional<String> getCodigoSeguranca();

  Optional<String> getTarefaId();

  Optional<String> getTarefa();
}