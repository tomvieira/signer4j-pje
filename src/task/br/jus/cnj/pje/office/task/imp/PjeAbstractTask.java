package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.IMainParams.PJE_MAIN_REQUEST_PARAM;
import static com.github.progress4j.IProgress.CANCELED_OPERATION_MESSAGE;
import static com.github.utils4j.imp.Strings.empty;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.signer4j.gui.alert.MessageAlert;
import com.github.signer4j.gui.alert.PermissionDeniedAlert;
import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.taskresolver4j.imp.AbstractTask;
import com.github.utils4j.gui.imp.ExceptionAlert;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.core.imp.PjeClientMode;
import br.jus.cnj.pje.office.core.imp.PjeConfig;
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
    
    DOWNLOADING("Baixando"),
    
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
    super(request.of(POJO_REQUEST_PARAM_NAME, pojo));
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
  
  protected final IPjeTarget getTarget(String url) {
    return new PjeTarget(getEndpointFor(url), getUserAgent(), getSession());
  }
  
  protected final IPjeTarget getExternalTarget(String url) {
    return new PjeTarget(url, getUserAgent(), "");
  }
  
  protected final void throwCancel() throws InterruptedException {
    throwCancel(CANCELED_OPERATION_MESSAGE);
  }
  
  protected final void throwCancel(String message) throws InterruptedException {
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
  
  protected final Optional<File> download(final IPjeTarget target) throws TaskException {
    return download(target, null);
  }    

  protected final void showInfo(String message) {
    MessageAlert.showInfo(message);
  }
  
  protected final void showInfo(String message, String textButton) {
    MessageAlert.showInfo(message, textButton);
  }

  protected final TaskException showFail(String message) {
    return showFail(message, message, null);
  }
  
  protected final TaskException showFail(String message, Throwable cause) {
    return showFail(message, empty(), cause);
  }
  
  protected final TaskException showFail(String message, String detail) {
    return showFail(message, detail, null);
  }
  
  protected final TaskException showFail(String message, String detail, Throwable cause) {
    ExceptionAlert.show(PjeConfig.getIcon(), message, detail, cause);
    return new TaskException(message + "\n" + detail, cause);
  }

  protected final Optional<File> download(final IPjeTarget target, File saveAt) {
    Args.requireNonNull(target, "target is null");
    final IProgress progress = getProgress();
    
    final DownloadStatus status = new DownloadStatus(saveAt) {
      private long total;
      private int increment = 1;
      
      @Override
      protected void onStepStart(long total) throws InterruptedException {
        this.total = total;
        progress.begin(Stage.DOWNLOADING, 100);
      }
      
      @Override
      protected void onStepEnd() throws InterruptedException {
        progress.end();
      }
      
      @Override
      protected void onStepStatus(long written) throws InterruptedException { 
        float percent = 100f * written / total;
        if (percent >= increment) {
          progress.step("Baixados %d%%", increment++);
        }
      }
    };
    
    try {
      getPjeClient().down(target, status);
    } catch (PJeClientException e) {
      progress.abort(e);
    }
    
    return status.getDownloadedFile();
  }
  
  protected final void checkMainParams() throws TaskException {
    if (!isInternalTask) {
      IMainParams main = getMainRequest();
      PjeTaskChecker.checkIfPresent(main.getServidor(), "servidor");
      PjeTaskChecker.checkIfPresent(main.getCodigoSeguranca(), "codigoSeguranca");
      PjeTaskChecker.checkIfPresent(main.getAplicacao(), "aplicacao");
    }
  }

  private final void checkParams() throws TaskException, InterruptedException {
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
      
      onBeforeDoGet();
      
      progress.begin(Stage.TASK_EXECUTION, 2);
      progress.step("Executando a tarefa '%s'", getId());
      ITaskResponse<IPjeResponse> response = doGet(); 
      progress.step("Tarefa completa. Status de sucesso: %s", response.isSuccess());
      progress.end();
      
      return response;
    } catch(InterruptedException | InterruptedSigner4JRuntimeException e) {
      fail = progress.abort(e);
      MessageAlert.showFail(CANCELED_OPERATION_MESSAGE);
    } catch(Exception e) {
      fail = progress.abort(e);
    }
    LOGGER.error("Não foi possível executar a tarefa " + getId(), fail);
    return fail(fail);
  }
  
  protected void onBeforeDoGet() {}

  protected void checkServerPermission() throws TaskException {
    if (this.isInternalTask) {
      return;
    }
    final IMainParams params = getMainRequest();
    StringBuilder whyNot = new StringBuilder();
    if (!getSecurityAgent().isPermitted(params, whyNot)) {
      String cause = whyNot.toString();
      if (!cause.isEmpty()) {
        PermissionDeniedAlert.showInfo(cause);
      }
      throw new TaskException("Permissão negada. " + cause);
    }
  }

  protected abstract void validateParams() throws TaskException, InterruptedException;
  
  protected abstract ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException;
}
