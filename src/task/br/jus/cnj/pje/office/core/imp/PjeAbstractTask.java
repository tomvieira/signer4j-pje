package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.IMainParams.PJE_MAIN_REQUEST_PARAM;
import static com.github.signer4j.gui.alert.PermissionDeniedAlert.display;
import static com.github.signer4j.imp.SwingTools.invokeLater;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;
import com.github.signer4j.task.imp.AbstractTask;

import br.jus.cnj.pje.office.core.IMainParams;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.core.ITaskExecutorParams;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.web.IPjeResponse;

abstract class PjeAbstractTask<T> extends AbstractTask<IPjeResponse>{

  protected static final Logger LOGGER = LoggerFactory.getLogger(PjeAbstractTask.class);
  
  private static final String POJO_REQUEST_PARAM_NAME = PjeAbstractTask.class.getSimpleName() + ".pojo";
  
  private static enum Stage implements IStage {
    
    PREPARING_PARAMETERS("Validação de parâmetros"),

    PERMISSION_CHECKING("Checagem de permissões"),
    
    TASK_EXECUTION("Execução da tarefa");
    
    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  };
  
  protected PjeAbstractTask(Params request, T pojo) {
    super(request);
    request.of(POJO_REQUEST_PARAM_NAME, pojo);
  }
  
  @Override
  public final String getId() {
    return getMainRequest().getTarefaId().get();
  }
  
  private final IMainParams getMainRequest() {
    return getParameterValue(PJE_MAIN_REQUEST_PARAM);
  }
  
  private final IPjeTokenAccess getTokenAccess() {
    return getParameterValue(IPjeTokenAccess.PARAM_NAME);
  }

  private final String getServerAddress() {
    return getMainRequest().getServidor().get();
  }
  
  protected final AtomicBoolean getLocalRequest() {
    return getParameterValue(ITaskExecutorParams.PJE_REQUEST_LOCAL);
  }
  
  protected final ExecutorService getRequestExecutor() {
    return getParameterValue(ITaskExecutorParams.PJE_REQUEST_EXECUTOR);
  }
  
  protected final void run(Runnable runnable) {
    getRequestExecutor().execute(runnable);
  }
  
  protected final IPjeSecurityAgent getSecurityAgent() {
    return getParameterValue(IPjeSecurityAgent.PARAM_NAME);
  }
  
  protected final String getEndpointFor(String sendTo) {
    return getServerAddress() + sendTo;
  }

  protected final String getUserAgent() {
    return getParameter(HttpHeaders.USER_AGENT).orElse(IPjeClient.PJE_DEFAULT_USER_AGENT);
  }
  
  protected final String getSession() {
    //Browser can send empty document.cookie, so we handle it as empty string
    return getMainRequest().getSessao().orElse(Strings.empty()); 
  }
  
  protected final IPjeToken loginToken() {
    return getTokenAccess().get();
  }

  protected final IPjeClient getPjeClient() {
    return PjeClientMode.clientFrom(getServerAddress());
  }
  
  protected final T getPojoParams() {
    return getParameterValue(POJO_REQUEST_PARAM_NAME);
  }
  
  protected void checkMainParams() throws TaskException {
    IMainParams main = getMainRequest();
    PjeTaskChecker.checkIfPresent(main.getServidor(), "servidor");
    PjeTaskChecker.checkIfPresent(main.getCodigoSeguranca(), "codigoSeguranca");
    PjeTaskChecker.checkIfPresent(main.getAplicacao(), "aplicacao");
  }

  protected final void checkParams() throws TaskException {
    checkMainParams();
    validateParams();
  }
  
  @Override
  public final ITaskResponse<IPjeResponse> get() {
    final IProgress progress = getProgress();
    try {
      progress.begin(Stage.PREPARING_PARAMETERS);
      progress.step("Preparando parâmetros de execução");
      checkParams();
      progress.step("Principais parâmetros validados");
      progress.end();
      
      progress.begin(Stage.PERMISSION_CHECKING);
      progress.step("Checando permissões de acesso ao servidor");
      checkServerPermission();
      progress.step("Acesso permitido");
      progress.end();
      
      progress.begin(Stage.TASK_EXECUTION);
      progress.step("Executando a tarefa '%s'", getId());
      ITaskResponse<IPjeResponse> response = doGet(); 
      progress.step("Tarefa completa. Status de sucesso: %s", PjeResponse.SUCCESS.equals(response));
      progress.end();
      
      return response;
    } catch(Exception e) {
      progress.abort(e);
      LOGGER.error("Nâo foi possível executar a tarefa " + getId(), e);
      return PjeResponse.FAIL;
    }
  }
  
  protected void checkServerPermission() throws TaskException {
    final IMainParams params = getMainRequest();
    StringBuilder whyNot = new StringBuilder();
    if (!getSecurityAgent().isPermitted(params, whyNot)) {
      String cause = whyNot.toString();
      if (!cause.isEmpty()) {
        invokeLater(() -> display(cause));
      }
      throw new TaskException("Permissão negada. " + cause);
    }
  }

  protected abstract void validateParams() throws TaskException;
  
  protected abstract ITaskResponse<IPjeResponse> doGet() throws TaskException;
}
