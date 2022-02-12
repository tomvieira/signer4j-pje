package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Threads.async;
import static com.github.signer4j.imp.Throwables.tryRun;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IFinishable;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.imp.Threads;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.imp.ProgressFactory;
import com.github.signer4j.task.ITaskRequestExecutor;

import br.jus.cnj.pje.office.core.IPjeCommander;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.task.imp.PjeTaskRequestExecutor;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class PjeCommander<I extends IPjeRequest, O extends IPjeResponse>  implements IPjeCommander<I, O> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(IPjeCommander.class);
  
  private final String serverEndpoint;

  private final IFinishable finishingCode;

  protected final ITaskRequestExecutor<IPjeRequest, IPjeResponse> executor;
  
  private final BehaviorSubject<LifeCycle> startup = BehaviorSubject.create();

  
  protected PjeCommander(IFinishable finishingCode, String serverAddress) {
    this(finishingCode, serverAddress, PjeCertificateAcessor.INSTANCE, PjeSecurityAgent.INSTANCE);
  }
  
  protected PjeCommander(IFinishable finishingCode, String serverAddress, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent) {
    this(finishingCode, serverAddress, tokenAccess, securityAgent, ProgressFactory.DEFAULT);
  }

  protected PjeCommander(IFinishable finishingCode, String serverAddress, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent, IProgressFactory factory) {
    this(new PjeTaskRequestExecutor(factory,  tokenAccess, securityAgent), finishingCode, serverAddress);
  }
  
  private PjeCommander(PjeTaskRequestExecutor executor, IFinishable finishingCode, String serverAddress) {
    this.executor = Args.requireNonNull(executor, "executor is null");
    this.finishingCode = Args.requireNonNull(finishingCode, "finishingCode is null");
    this.serverEndpoint = Args.requireText(serverAddress, "serverAddress is empty");
  }
  
  protected final String getServerEndpoint() {
    return serverEndpoint;
  }

  protected final String getServerEndpoint(String path) {
    return serverEndpoint + Strings.trim(path);
  }

  protected final void notifyShutdown() {
    startup.onNext(LifeCycle.SHUTDOWN);
  }
  
  protected final void notifyStartup() {
    startup.onNext(LifeCycle.STARTUP);
  }
  
  protected final void notifyKill() {
    startup.onNext(LifeCycle.KILL);
  }
  
  @Override
  public final Observable<LifeCycle> lifeCycle() {
    return startup;
  }
  
  @Override
  public final void exit() {
    finishingCode.exit(1500);
  }
  
  @Override
  public final void logout() {
    finishingCode.logout();
  }
  
  @Override
  public void execute(I request, O response) {
    try {
      this.executor.execute(request, response);
    } catch (Exception e) {
      handleException(request, response, e);
    }
  }
  
  protected void handleException(I request, O response, Exception e) {
    LOGGER.error("Exceção no ciclo de vida da requisição", e);
  }
  
  @Override
  public synchronized void stop(boolean kill) {
    tryRun(executor::close);
    tryRun(this::notifyShutdown);
    if (kill) {
      tryRun(this::notifyKill);
    }
  }
  
  @Override
  public final void showOfflineSigner() {
    final String request = 
      "{\"aplicacao\":\"Pje\"," + 
      "\"servidor\":\"nothing://\"," + 
      "\"sessao\":\"\"," + 
      "\"codigoSeguranca\":\"localhost\"," + 
      "\"tarefaId\":\"cnj.assinador\"," + 
      "\"tarefa\":\"{\\\"modo\\\":\\\"local\\\","
      + "\\\"padraoAssinatura\\\":\\\"NOT_ENVELOPED\\\","
      + "\\\"tipoAssinatura\\\":\\\"ATTACHED\\\","
      + "\\\"algoritmoHash\\\":\\\"MD5withRSA\\\"}\"" + 
      "}&u=" + System.currentTimeMillis();
    String encodedRequest = Strings.get(() -> encode(request, UTF_8.toString()), "").get();
    async(() ->  {
      try {
        this.executor.setAllowLocalRequest(true);
        openSigner("?r=" + encodedRequest);
      }finally {
        Threads.sleep(1000);   
        this.executor.setAllowLocalRequest(false);
      }
    });
  }    
  
  protected abstract void openSigner(String encodedRequest);
}
