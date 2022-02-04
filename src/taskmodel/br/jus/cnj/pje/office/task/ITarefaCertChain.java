package br.jus.cnj.pje.office.task;

import java.util.Optional;

public interface ITarefaCertChain {
  
  public Optional<String> getUploadUrl();
  
  @Deprecated
  public boolean isDeslogarKeyStore();
}