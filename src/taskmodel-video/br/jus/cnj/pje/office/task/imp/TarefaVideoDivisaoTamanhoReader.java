package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.VIDEO_SPLIT_BY_SIZE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoTamanho;


/*************************************************************************************
 * Leitor para divisão de vídeos por tamanho específico
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
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    long tamanho = Strings.toLong(second(argumetns).orElse(""), -1);
    if (tamanho <= 0) {
      throw new IllegalArgumentException("'tamanho' não informado");
    }
    
    TarefaVideoDivisaoTamanho tarefaTamanho = new TarefaVideoDivisaoTamanho();
    tarefaTamanho.tamanho = tamanho;
    tarefaTamanho.arquivos = flatFirst(argumetns);
    return tarefaTamanho;
  }
}