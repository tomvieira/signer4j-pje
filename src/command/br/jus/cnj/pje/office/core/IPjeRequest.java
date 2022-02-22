package br.jus.cnj.pje.office.core;

import java.util.Optional;

public interface IPjeRequest {
  String PJE_REQUEST_PARAMETER_NAME = "r";

  String PJE_REQUEST_PARAMETER_CACHE = "u";
  
  String getId();
  
  Optional<String> getParameterR();
  
  Optional<String> getParameterU();
  
  Optional<String> getUserAgent();

  Optional<String> getOrigin();
}
