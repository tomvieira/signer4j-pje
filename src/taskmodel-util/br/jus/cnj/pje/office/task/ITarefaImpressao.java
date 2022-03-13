package br.jus.cnj.pje.office.task;

import java.util.List;
import java.util.Optional;

public interface ITarefaImpressao {
  List<String> getConteudo();
  
  Optional<String> getImpressora();
}
