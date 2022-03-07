package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_COUNT;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoContagem;

/*************************************************************************************
 * Leitor para divisão de pdf por contagem de páginas
/*************************************************************************************/

class TarefaPdfDivisaoContagemReader extends TarefaMediaReader<ITarefaPdfDivisaoContagem> {

  public static final TarefaPdfDivisaoContagemReader INSTANCE = new TarefaPdfDivisaoContagemReader();
  
  final static class TarefaPdfDivisaoContagem extends TarefaMedia implements ITarefaPdfDivisaoContagem {
    private long totalPaginas;

    @Override
    public long getTotalPaginas() {
      return totalPaginas;
    }
  }
  
  private TarefaPdfDivisaoContagemReader() {
    super(TarefaPdfDivisaoContagem.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoContagem pojo) throws IOException {
    return new PjeByCountPdfSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_COUNT.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    String total = second(argumetns).orElse("");
    if (!Strings.isLong(total)) {
      throw new IllegalArgumentException("'totalPaginas' não informado");
    }
    long totalPaginas = Long.valueOf(total);
    TarefaPdfDivisaoContagem tarefaTamanho = new TarefaPdfDivisaoContagem();
    tarefaTamanho.totalPaginas = totalPaginas;
    tarefaTamanho.arquivos = flatFirst(argumetns);
    return tarefaTamanho;
  }
}
