package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_PAGES;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaMedia;

/*************************************************************************************
 * Leitor para Divisão de PDF por página
/*************************************************************************************/

class TarefaPdfDivisaoPaginasReader extends TarefaMediaReader<ITarefaMedia> {

  public static final TarefaPdfDivisaoPaginasReader INSTANCE = new TarefaPdfDivisaoPaginasReader();
  
  private TarefaPdfDivisaoPaginasReader() {
    super(TarefaMedia.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaMedia pojo) throws IOException {
    return new PjeByPagesPdfSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_PAGES.getId();
  }
  
  @Override
  protected Object getTarefa(Params param) {
    TarefaMedia divisao = new TarefaMedia();
    divisao.arquivos = param.getValue("arquivos");
    return divisao;
  }
}