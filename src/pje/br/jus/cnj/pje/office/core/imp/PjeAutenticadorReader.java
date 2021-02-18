package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Strings.optional;

import java.io.IOException;
import java.util.Optional;

import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.core.IAutenticadorParams;

class PjeAutenticadorReader extends AbstractRequestReader<Params, PjeAutenticadorReader.TarefaAutenticador>{

  public static final PjeAutenticadorReader INSTANCE = new PjeAutenticadorReader();

  static final class TarefaAutenticador implements IAutenticadorParams {
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

  private PjeAutenticadorReader() {
    super(TarefaAutenticador.class);
  }

  @Override
  protected ITask<?> createTask(Params output, TarefaAutenticador pojo) throws IOException{
    return new PjeAutenticadorTask(output, pojo);
  }
}
