package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;

import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;

import br.jus.cnj.pje.office.task.ITarefaAutenticador;

class TarefaAutenticadorSSOReader extends TarefaAutenticadorReader{

  public static final TarefaAutenticadorSSOReader INSTANCE = new TarefaAutenticadorSSOReader();

  private TarefaAutenticadorSSOReader() {
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaAutenticador pojo) throws IOException{
    return new PjeAutenticatorSSOTask(output, pojo);
  }
}
