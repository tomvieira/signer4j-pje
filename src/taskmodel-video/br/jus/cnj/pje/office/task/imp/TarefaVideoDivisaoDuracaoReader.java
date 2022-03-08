package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.util.List;

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
    List<String> arquivos = getFiles(param, "Arquivos não informados");
    long duracao = getLong(param, "'duracao' com valor iválido ");
    
    TarefaVideoDivisaoDuracao tarefaDuracao = new TarefaVideoDivisaoDuracao();
    tarefaDuracao.duracao = duracao;
    tarefaDuracao.arquivos = arquivos;
    return tarefaDuracao;
  }
}
