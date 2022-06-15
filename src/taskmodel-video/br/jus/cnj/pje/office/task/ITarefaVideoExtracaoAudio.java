package br.jus.cnj.pje.office.task;

import java.util.Optional;

public interface ITarefaVideoExtracaoAudio extends ITarefaMedia {
  Optional<String> getTipo();
}
