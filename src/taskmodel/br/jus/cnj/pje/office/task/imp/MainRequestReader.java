package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.IMainParams.PJE_MAIN_REQUEST_PARAM;
import static com.github.signer4j.imp.Strings.optional;

import java.io.IOException;
import java.util.Optional;

import com.github.signer4j.IParam;
import com.github.signer4j.imp.Params;
import com.github.signer4j.task.IRequestReader;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.task.IMainParams;

public class MainRequestReader extends AbstractRequestReader<Params, IMainParams>{

  public static final MainRequestReader MAIN = new MainRequestReader();

  static final class MainRequest implements IMainParams {
    private String aplicacao;
    private String servidor;
    private String sessao;
    private String codigoSeguranca;
    private String tarefaId;
    private String tarefa;

    @Override
    public Optional<String> getServidor() {
      return optional(this.servidor);
    }

    @Override
    public Optional<String> getAplicacao() {
      return optional(this.aplicacao);
    }

    @Override
    public Optional<String> getSessao() {
      return optional(this.sessao);
    }

    @Override
    public Optional<String> getCodigoSeguranca() {
      return optional(this.codigoSeguranca);
    }

    @Override
    public Optional<String> getTarefaId() {
      return optional(this.tarefaId);
    }

    @Override
    public Optional<String> getTarefa() {
      return optional(this.tarefa);
    }

    @Override
    public Optional<String> getOrigin() {
      return Optional.empty();
    }
  }

  private MainRequestReader() {
    super(MainRequest.class);
  }

  @Override
  protected ITask<?> createTask(Params output, IMainParams main) throws IOException {
    output.of(PJE_MAIN_REQUEST_PARAM, main);
    
    Optional<String> app = main.getAplicacao();
    if (!app.isPresent()) {
      throw new IOException("Server did not send 'aplicacao' parameter");
    }
    
    Optional<String> taskId = main.getTarefaId();
    if (!taskId.isPresent()) {
      throw new IOException("Server did not send the 'tarefaId' parameter!");
    }
    
    Optional<String> task = main.getTarefa();
    if (!task.isPresent()) {
      throw new IOException("Server did not send the 'tarefa' parameter!");
    }
    
    IRequestReader<Params> reader = PjeTaskReaders.from(taskId.get());
    
    reader.read(task.get(), output);
    
    IParam taskParam = output.get(ITask.PARAM_NAME);
    
    if (!taskParam.isPresent()) {
      throw new IOException("Unabled to create instance of 'idTarefa': " + taskId.get());
    }
    
    return taskParam.get();
  }
}
