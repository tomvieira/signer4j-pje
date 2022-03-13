package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoDuracao;


class TarefaVideoDivisaoDuracaoReader extends TarefaMediaReader<ITarefaVideoDivisaoDuracao> {

  public static final TarefaVideoDivisaoDuracaoReader INSTANCE = new TarefaVideoDivisaoDuracaoReader();
  
  final static class TarefaVideoDivisaoDuracao extends TarefaMedia implements ITarefaVideoDivisaoDuracao {
    private long duracao;

    @Override
    public long getDuracao() {
      return duracao;
    }
  }
  
  private TarefaVideoDivisaoDuracaoReader() {
    super(TarefaVideoDivisaoDuracao.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaVideoDivisaoDuracao pojo) throws IOException {
    return new PjeByDurationVideoSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PjeTaskReader.VIDEO_SPLIT_BY_DURATION.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    TarefaVideoDivisaoDuracao tarefaDuracao = new TarefaVideoDivisaoDuracao();
    tarefaDuracao.duracao = Long.parseLong(param.getValue("duracao"));
    tarefaDuracao.arquivos = param.getValue("arquivos");
    return tarefaDuracao;
  }
}
