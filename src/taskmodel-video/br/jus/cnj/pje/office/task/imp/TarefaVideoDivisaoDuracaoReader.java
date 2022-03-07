package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

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
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    String d = second(argumetns).orElse("");
    if (!Strings.isLong(d)) {
      throw new IllegalArgumentException("'duracao' não informado");
    }
    long duracao = Long.valueOf(d);
    TarefaVideoDivisaoDuracao tarefaDuracao = new TarefaVideoDivisaoDuracao();
    tarefaDuracao.duracao = duracao;
    tarefaDuracao.arquivos = flatFirst(argumetns);
    return tarefaDuracao;
  }
}
