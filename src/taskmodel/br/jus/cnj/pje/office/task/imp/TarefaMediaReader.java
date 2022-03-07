package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;
import static com.github.utils4j.imp.Strings.empty;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.IJsonTranslator;
import br.jus.cnj.pje.office.task.ITarefaMedia;

/*************************************************************************************
 * Classe base para todos os manipuladores de medias (PDF's, Vídeos, etc)
/*************************************************************************************/

abstract class TarefaMediaReader<T extends ITarefaMedia> extends AbstractRequestReader<Params, T> implements IJsonTranslator {

  static class TarefaMedia implements ITarefaMedia {
    protected List<String> arquivos = new ArrayList<>();
    
    @Override
    public final List<String> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }

  protected Optional<String> first(List<String[]> params) {
    return Strings.optional(params.isEmpty() ? empty() : flatFirst(params).get(0));
  }
  
  protected Optional<String> second(List<String[]> params) {
    return Strings.optional(params.isEmpty() ? empty() : flatSecond(params).get(0));
  }

  protected Optional<String> third(List<String[]> params) {
    return Strings.optional(params.isEmpty() ? empty() : flatSecond(params).get(0));
  }

  protected static List<String> flatFirst(List<String[]> params) { 
    return flatIndex(params, 0);
  }

  protected static List<String> flatSecond(List<String[]> params) { 
    return flatIndex(params, 1);
  }
  
  protected static List<String> flatThird(List<String[]> params) { 
    return flatIndex(params, 2);
  }

  private static List<String> flatIndex(List<String[]> list, int index) {
    return list.stream()
      .map(a -> index < a.length ? a[index] : empty())
      .collect(toList());
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


