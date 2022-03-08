package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_PARITY;

import java.io.IOException;
import java.util.List;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoParidade;

/*************************************************************************************
 * Leitor para divisão de pdf por PARIDADE
/*************************************************************************************/

class TarefaPdfDivisaoParidadeReader extends TarefaMediaReader<ITarefaPdfDivisaoParidade> {

  public static final TarefaPdfDivisaoParidadeReader INSTANCE = new TarefaPdfDivisaoParidadeReader();
  
  final static class TarefaPdfDivisaoParidade extends TarefaMedia implements ITarefaPdfDivisaoParidade {
    private boolean paridade;

    @Override
    public boolean isParidade() {
      return paridade;
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
    List<String> arquivos= getFiles(param, "Arquivos não informados");
    boolean paridade = getBoolean(param,"'paridade' com valor inválido");
    
    TarefaPdfDivisaoParidade tarefaTamanho = new TarefaPdfDivisaoParidade();
    tarefaTamanho.paridade = paridade;
    tarefaTamanho.arquivos = arquivos;
    return tarefaTamanho;
  }
}
