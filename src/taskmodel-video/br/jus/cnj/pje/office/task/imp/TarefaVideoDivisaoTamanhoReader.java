package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.VIDEO_SPLIT_BY_SIZE;

import java.io.IOException;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoTamanho;


/*************************************************************************************
 * Leitor para divisão de VÍDEOS por tamanho específico
/*************************************************************************************/

class TarefaVideoDivisaoTamanhoReader extends TarefaMediaReader<ITarefaVideoDivisaoTamanho> {

  public static final TarefaVideoDivisaoTamanhoReader INSTANCE = new TarefaVideoDivisaoTamanhoReader();
  
  final static class TarefaVideoDivisaoTamanho extends TarefaMedia implements ITarefaVideoDivisaoTamanho {
    private long tamanho;
    
    public final long getTamanho(){
      return tamanho;
    }
  }
  
  private TarefaVideoDivisaoTamanhoReader() {
    super(TarefaVideoDivisaoTamanho.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaVideoDivisaoTamanho pojo) throws IOException {
    return new PjeBySizeVideoSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return VIDEO_SPLIT_BY_SIZE.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    List<String> arquivos = getFiles(param, "Arquivos não informados");
    long tamanho = getLong(param, "'tamanho' informado é inválido");
    
    TarefaVideoDivisaoTamanho tarefaTamanho = new TarefaVideoDivisaoTamanho();
    tarefaTamanho.tamanho = tamanho;
    tarefaTamanho.arquivos = arquivos;
    return tarefaTamanho;
  }
}