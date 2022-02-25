package br.jus.cnj.pje.office.task.imp;

import static com.github.utils4j.imp.Strings.optional;

import java.io.IOException;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaAutenticador;

class TarefaAutenticadorReader extends AbstractRequestReader<Params, ITarefaAutenticador>{

  public static final TarefaAutenticadorReader INSTANCE = new TarefaAutenticadorReader();

  static final class TarefaAutenticador implements ITarefaAutenticador {
    private String enviarPara;
    private String mensagem;
    private String token;
    private String algoritmoAssinatura;

    @Override
    public final Optional<String> getAlgoritmoAssinatura() {
      return optional(this.algoritmoAssinatura);
    }

    @Override
    public final Optional<String> getEnviarPara() {
      return optional(this.enviarPara);
    }

    @Override
    public final Optional<String> getMensagem() {
      return optional(this.mensagem);
    }

    @Override
    public final Optional<String> getToken() {
      return optional(this.token);
    }
  }

  protected TarefaAutenticadorReader() {
    super(TarefaAutenticador.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaAutenticador pojo) throws IOException{
    return new PjeAutenticatorTask(output, pojo);
  }
}
