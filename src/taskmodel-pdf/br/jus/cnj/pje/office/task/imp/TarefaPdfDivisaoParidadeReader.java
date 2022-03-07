package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_PARITY;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoParidade;

/*************************************************************************************
 * Leitor para divisão de pdf por PARIDADE
/*************************************************************************************/

class TarefaPdfDivisaoParidadeReader extends TarefaMediaReader<ITarefaPdfDivisaoParidade> {

  public static final TarefaPdfDivisaoParidadeReader INSTANCE = new TarefaPdfDivisaoParidadeReader();
  
  final static class TarefaPdfDivisaoParidade extends TarefaMedia implements ITarefaPdfDivisaoParidade {
    private boolean par;

    @Override
    public boolean isPar() {
      return par;
    }
  }
  
  private TarefaPdfDivisaoParidadeReader() {
    super(TarefaPdfDivisaoParidade.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoParidade pojo) throws IOException {
    return new PjeByParityPdfSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_PARITY.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    String par = second(argumetns).orElse("");
    if (!Strings.isBoolean(par)) {
      throw new IllegalArgumentException("'paridade' não informado");
    }
    boolean isPar = Boolean.valueOf(par);
    TarefaPdfDivisaoParidade tarefaTamanho = new TarefaPdfDivisaoParidade();
    tarefaTamanho.par = isPar;
    tarefaTamanho.arquivos = flatFirst(argumetns);
    return tarefaTamanho;
  }
}
