package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IJsonTranslator;
import br.jus.cnj.pje.office.task.ITarefaMedia;

/*************************************************************************************
 * Classe base para todos os manipuladores de medias (PDF's, VÍDEOS, etc)
/*************************************************************************************/

abstract class TarefaMediaReader<T extends ITarefaMedia> extends AbstractRequestReader<Params, T> implements IJsonTranslator {

  static class TarefaMedia implements ITarefaMedia {
    protected List<String> arquivos = new ArrayList<>();
    
    @Override
    public final List<String> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }

  protected TarefaMediaReader(Class<?> clazz) {
    super(clazz);
  }
  
  @Override
  public String toJson(Params param) throws Exception {
    return MAIN.toJson(param
      .of("tarefaId", getTarefaId())
      .of("tarefa", getTarefa(param))
    );
  }

  protected abstract String getTarefaId();
  protected abstract Object getTarefa(Params input);
}


