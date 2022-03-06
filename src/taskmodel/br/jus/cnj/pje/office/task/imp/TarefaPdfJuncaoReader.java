package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;
import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_JOIN;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IJsonTranslator;
import br.jus.cnj.pje.office.task.ITarefaPdfJuncao;

class TarefaPdfJuncaoReader extends AbstractRequestReader<Params, ITarefaPdfJuncao> implements IJsonTranslator {

  public static final TarefaPdfJuncaoReader INSTANCE = new TarefaPdfJuncaoReader();
  
  final static class TarefaPdfJuncao implements ITarefaPdfJuncao {
    private List<String> arquivos = new ArrayList<>();
    
    @Override
    public final List<String> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }
  
  private TarefaPdfJuncaoReader() {
    super(TarefaPdfJuncao.class);
  }
  
  
  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfJuncao pojo) throws IOException {
    return new PjeJoinPdfTask(output, pojo);
  }

  @Override
  public String toJson(Params input) throws Exception {
    TarefaPdfJuncao juncao = new TarefaPdfJuncao();
    juncao.arquivos = input.orElse("arquivos", Collections.<String>emptyList());
    return MAIN.toJson(input
      .of("tarefaId", PDF_JOIN.getId())
      .of("tarefa", juncao)
    );
  }
}
