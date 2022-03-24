package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.VIDEO_SPLIT_BY_SLICE;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaMedia;


/*************************************************************************************
 * Leitor para divisão de VÍDEOS por fatias/cortes específicos
/*************************************************************************************/

class TarefaVideoDivisaoSliceReader extends TarefaMediaReader<ITarefaMedia> {

  public static final TarefaVideoDivisaoSliceReader INSTANCE = new TarefaVideoDivisaoSliceReader();
  
  private TarefaVideoDivisaoSliceReader() {
    super(TarefaMedia.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaMedia pojo) throws IOException {
    return new PjeBySliceVideoSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return VIDEO_SPLIT_BY_SLICE.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    TarefaMedia tarefaSlice = new TarefaMedia();
    tarefaSlice.arquivos = param.getValue("arquivos");
    return tarefaSlice;
  }
}