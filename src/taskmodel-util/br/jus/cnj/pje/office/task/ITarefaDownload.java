package br.jus.cnj.pje.office.task;

import java.util.Optional;

public interface ITarefaDownload {
  Optional<String> getUrl();
  Optional<String> getEnviarPara();
}
