package br.jus.cnj.pje.office.core.imp;

import com.github.signer4j.imp.Caller;

enum PjeResultChecker implements Caller<String, Void, PjeServerException> {
  
  IF_ERROR_THROW() {
    @Override
    public Void call(String response) throws PjeServerException {
      final int length = response.length();
      if (response.startsWith(SERVER_RESPONSE_TEXT_FAIL)) { 
        String message = length > SERVER_RESPONSE_TEXT_FAIL.length() ? 
          response.substring(SERVER_RESPONSE_TEXT_FAIL.length()) : 
          "Desconhecido";
        throw new PjeServerException("Servidor retornou Erro: '" + message.trim() + "'");
      }
      return null;
    }
  },
  
  IF_NOT_SUCCESS_THROW() {
    @Override
    public Void call(String response) throws PjeServerException {
      if (!response.startsWith(SERVER_RESPONSE_TEXT_SUCCESS)) {
        IF_ERROR_THROW.call(response);
        return null; //success!
      }
      throw new PjeServerException("Servidor não recebeu arquivo enviado");
    }
  };
  
  private static final String SERVER_RESPONSE_TEXT_SUCCESS = "Sucesso";
  private static final String SERVER_RESPONSE_TEXT_FAIL = "Erro:"; 
}