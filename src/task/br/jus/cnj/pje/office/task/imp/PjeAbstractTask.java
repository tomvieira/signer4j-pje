package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.IMainParams.PJE_MAIN_REQUEST_PARAM;
import static com.github.progress4j.IProgress.CANCELED_OPERATION_MESSAGE;
import static com.github.signer4j.gui.alert.MessageAlert.displayFail;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import java.util.concurrent.ExecutorService;

import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.signer4j.gui.alert.PermissionDeniedAlert;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.taskresolver4j.imp.AbstractTask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.core.imp.PjeClientMode;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.IMainParams;
import br.jus.cnj.pje.office.task.IPjeTarget;
import br.jus.cnj.pje.office.task.ITaskExecutorParams;

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
  
  private boolean isInternalTask;
  
  protected PjeAbstractTask(Params request, T pojo) {
    this(request, pojo, false);
  }
  
  protected PjeAbstractTask(Params request, T pojo, boolean isInternalTask) {
    super(request);
    request.of(POJO_REQUEST_PARAM_NAME, pojo);
    this.isInternalTask = isInternalTask;
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
  
  private ExecutorService getRequestExecutor() {
    return getParameterValue(ITaskExecutorParams.PJE_REQUEST_EXECUTOR);
  }
  
  private final String getEndpointFor(String sendTo) {
    return getServerAddress() + sendTo;
  }

  private final String getUserAgent() {
    return getParameter(HttpHeaders.USER_AGENT).orElse(IPjeClient.PJE_DEFAULT_USER_AGENT);
  }
  
  private final String getSession() {
    return getMainRequest().getSessao().orElse(Strings.empty()); 
  }
  
  protected final IPjeTarget getTarget(String sendTo) {
    return new PjeTarget(getEndpointFor(sendTo), getUserAgent(), getSession());
  }
  
  protected void throwCancel() throws InterruptedException {
    throwCancel(CANCELED_OPERATION_MESSAGE);
  }
  
  protected void throwCancel(String message) throws InterruptedException {
    Thread.currentThread().interrupt();
    throw getProgress().abort(new InterruptedException(message));
  }
  
  protected final void runAsync(Runnable runnable) {
    getRequestExecutor().execute(runnable);
  }
  
  protected final IPjeSecurityAgent getSecurityAgent() {
    return getParameterValue(IPjeSecurityAgent.PARAM_NAME);
  }
  
  protected final IPjeToken loginToken() {
    return getTokenAccess().get();
  }
  
  protected final void forceLogout() {
    getTokenAccess().logout();
  }

  protected final IPjeClient getPjeClient() {
    return PjeClientMode.clientFrom(getServerAddress(), getProgress());
  }
  
  protected final T getPojoParams() {
    return getParameterValue(POJO_REQUEST_PARAM_NAME);
  }
  
  protected final ITaskResponse<IPjeResponse> fail(Throwable exception) {
    return PjeClientMode.failFrom(getServerAddress()).apply(exception) ;
  }
  
  protected final PjeTaskResponse success() {
    return PjeClientMode.successFrom(getServerAddress()).apply("success");
  }

  protected final PjeTaskResponse success(String output) {
    return PjeClientMode.successFrom(getServerAddress()).apply("success: " + output);
  }
  
  protected void checkMainParams() throws TaskException {
    if (!isInternalTask) {
      IMainParams main = getMainRequest();
      PjeTaskChecker.checkIfPresent(main.getServidor(), "servidor");
      PjeTaskChecker.checkIfPresent(main.getCodigoSeguranca(), "codigoSeguranca");
      PjeTaskChecker.checkIfPresent(main.getAplicacao(), "aplicacao");
    }
  }

  protected final void checkParams() throws TaskException {
    checkMainParams();
    validateParams();
  }
  
  @Override
  public final String getId() {
    return getMainRequest().getTarefaId().get();
  }
  
  @Override
  public final ITaskResponse<IPjeResponse> get() {
    final IProgress progress = getProgress();
    Throwable fail;
    try {
      progress.begin(Stage.PREPARING_PARAMETERS, 2);
      progress.step("Preparando parâmetros de execução");
      checkParams();
      progress.step("Principais parâmetros validados");
      progress.end();
      
      progress.begin(Stage.PERMISSION_CHECKING, 2);
      progress.step("Checando permissões de acesso ao servidor");
      checkServerPermission();
      progress.step("Acesso permitido");
      progress.end();
      
      beforeGet();
      
      progress.begin(Stage.TASK_EXECUTION, 2);
      progress.step("Executando a tarefa '%s'", getId());
      ITaskResponse<IPjeResponse> response = doGet(); 
      progress.step("Tarefa completa. Status de sucesso: %s", response.isSuccess());
      progress.end();
      
      return response;
    } catch(InterruptedException e) {
      fail = progress.abort(e);
      invokeLater(() -> displayFail(CANCELED_OPERATION_MESSAGE));
    } catch(Exception e) {
      fail = progress.abort(e);
      invokeLater(() -> displayFail("Não foi possível realizar a operação:\n" + Strings.trim(e.getMessage())));
    }
    
    LOGGER.error("Não foi possível executar a tarefa " + getId(), fail);
    return fail(fail);
  }
  
  protected void beforeGet() {}

  protected void checkServerPermission() throws TaskException {
    if (this.isInternalTask) {
      return;
    }
    final IMainParams params = getMainRequest();
    StringBuilder whyNot = new StringBuilder();
    if (!getSecurityAgent().isPermitted(params, whyNot)) {
      String cause = whyNot.toString();
      if (!cause.isEmpty()) {
        invokeLater(() -> PermissionDeniedAlert.display(cause));
      }
      throw new TaskException("Permissão negada. " + cause);
    }
  }

  protected abstract void validateParams() throws TaskException;
  
  protected abstract ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException;
}
