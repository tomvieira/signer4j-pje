package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_SIZE;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoTamanho;

/*************************************************************************************
 * Leitor para divisão de pdf por tamanho específico PDFs
/*************************************************************************************/

class TarefaPdfDivisaoTamanhoReader extends TarefaMediaReader<ITarefaPdfDivisaoTamanho> {

  public static final TarefaPdfDivisaoTamanhoReader INSTANCE = new TarefaPdfDivisaoTamanhoReader();
  
  final static class TarefaPdfDivisaoTamanho extends TarefaMedia implements ITarefaPdfDivisaoTamanho {
    private long tamanho;
    
    public final long getTamanho(){
      return tamanho;
    }
  }
  
  private TarefaPdfDivisaoTamanhoReader() {
    super(TarefaPdfDivisaoTamanho.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoTamanho pojo) throws IOException {
    return new PjeBySizePdfSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_SIZE.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    TarefaPdfDivisaoTamanho tarefaTamanho = new TarefaPdfDivisaoTamanho();
    tarefaTamanho.tamanho = Long.parseLong(param.getValue("tamanho"));
    tarefaTamanho.arquivos = param.getValue("arquivos");
    return tarefaTamanho;
  }
}