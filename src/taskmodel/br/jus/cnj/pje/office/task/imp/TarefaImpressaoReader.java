package br.jus.cnj.pje.office.task.imp;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.task.ITarefaImpressao;

public class TarefaImpressaoReader extends AbstractRequestReader<Params, TarefaImpressaoReader.TarefaImpressao>{

  public static final TarefaImpressaoReader INSTANCE = new TarefaImpressaoReader();
  
  final static class TarefaImpressao implements ITarefaImpressao {
    private List<String> conteudo = new ArrayList<>();
    
    private String impressora = "LPT1";

    @Override
    public final List<String> getConteudo() {
      return this.conteudo == null ? emptyList() : unmodifiableList(this.conteudo);
    }

    @Override
    public Optional<String> getImpressora() {
      return Strings.optional(impressora);
    }
  }
  
  private TarefaImpressaoReader() {
    super(TarefaImpressao.class);
  }
  
  @Override
  protected ITask<?> createTask(Params output, TarefaImpressao pojo) throws IOException {
    return new PjePrintTask(output, pojo);
  }
}
