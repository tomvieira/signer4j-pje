package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_JOIN;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaMedia;

/*************************************************************************************
 * Leitor para Junção de PDFs
/*************************************************************************************/

class TarefaPdfJuncaoReader extends TarefaMediaReader<ITarefaMedia> {

  public static final TarefaPdfJuncaoReader INSTANCE = new TarefaPdfJuncaoReader();
  
  private TarefaPdfJuncaoReader() {
    super(TarefaMedia.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaMedia pojo) throws IOException {
    return new PjeJoinPdfTaskTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_JOIN.getId();
  }
  
  @Override
  protected Object getTarefa(Params input) {
    List<String[]> arguments = input.orElse("arguments", Collections.<String[]>emptyList());
    TarefaMedia juncao = new TarefaMedia();
    juncao.arquivos = flatFirst(arguments);
    return juncao;
  }
}