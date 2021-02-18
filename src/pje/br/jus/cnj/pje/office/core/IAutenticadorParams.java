package br.jus.cnj.pje.office.core;

import java.util.Optional;

public interface IAutenticadorParams {

  String PJE_TAREFA_AUTENTICADOR_PARAM = IAutenticadorParams.class.getSimpleName();
  
  Optional<String> getAlgoritmoAssinatura();

  Optional<String> getEnviarPara();

  Optional<String> getMensagem();

  Optional<String> getToken();
}