package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

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
