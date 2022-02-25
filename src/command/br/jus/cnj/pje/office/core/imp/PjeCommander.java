package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Strings.get;
import static com.github.utils4j.imp.Threads.async;
import static com.github.utils4j.imp.Throwables.tryRun;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.progress.IProgressFactory;
import com.github.taskresolver4j.ITaskRequestExecutor;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;
import com.github.utils4j.imp.Threads;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeCommander;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

abstract class PjeCommander<I extends IPjeRequest, O extends IPjeResponse>  implements IPjeCommander<I, O> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(IPjeCommander.class);

  private final String serverEndpoint;

  protected final IBootable boot;

  protected final ITaskRequestExecutor<IPjeRequest, IPjeResponse> executor;
  
  private final BehaviorSubject<LifeCycle> startup = BehaviorSubject.create();
  
  protected PjeCommander(IBootable boot, String serverEndpoint) {
    this(boot, serverEndpoint, PjeCertificateAcessor.INSTANCE, PjeSecurityAgent.INSTANCE);
  }
  
  protected PjeCommander(IBootable boot, String serverEndpoint, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent) {
    this(boot, serverEndpoint, tokenAccess, securityAgent, PjeProgressFactory.DEFAULT);
  }

  protected PjeCommander(IBootable boot, String serverEndpoint, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent, IProgressFactory factory) {
    this(new PjeTaskRequestExecutor(factory,  tokenAccess, securityAgent), boot, serverEndpoint);
  }
  
  private PjeCommander(PjeTaskRequestExecutor executor, IBootable boot, String serverEndpoint) {
    this.executor = Args.requireNonNull(executor, "executor is null");
    this.boot = Args.requireNonNull(boot, "boot is null");
    this.serverEndpoint = Args.requireText(serverEndpoint, "serverEndpoint is empty");
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
    boot.exit(1500);
  }
  
  @Override
  public final void logout() {
    boot.logout();
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
      "{\"aplicacao\":\"PjeOffice\"," + 
      "\"servidor\":\"" + serverEndpoint + "\"," + 
      "\"sessao\":\"\"," + 
      "\"codigoSeguranca\":\"localhost\"," + 
      "\"tarefaId\":\"cnj.assinador\"," + 
      "\"tarefa\":\"{\\\"modo\\\":\\\"local\\\","
      + "\\\"padraoAssinatura\\\":\\\"NOT_ENVELOPED\\\","
      + "\\\"tipoAssinatura\\\":\\\"ATTACHED\\\","
      + "\\\"algoritmoHash\\\":\\\"MD5withRSA\\\"}\"" + 
      "}";
    
//    final String request = 
//        "{\"aplicacao\":\"PjeOffice\"," + 
//        "\"servidor\":\"" + serverEndpoint + "\"," + 
//        "\"sessao\":\"\"," + 
//        "\"codigoSeguranca\":\"localhost\"," + 
//        "\"tarefaId\":\"cnj.assinador\"," + 
//        "\"tarefa\":\"{\\\"modo\\\":\\\"definido\\\","
//        + "\\\"padraoAssinatura\\\":\\\"NOT_ENVELOPED\\\","
//        + "\\\"tipoAssinatura\\\":\\\"ATTACHED\\\","
//        + "\\\"algoritmoHash\\\":\\\"MD5withRSA\\\","
//        + "\\\"arquivos\\\":["
//        + "{\\\"nome\\\":\\\"primeiro\\\",\\\"url\\\":\\\"D:/generics.pdf\\\"}"
//        + "],"
//        + "\\\"enviarPara\\\":\\\"D:/\\\"}\"" + 
//        "}";
    
    async(() ->  {
      String encodedRequest = get(() -> encode(request, UTF_8.toString()), "").get();
      try {
        this.executor.setAllowLocalRequest(true);
        openSigner("?r=" + encodedRequest + "&u=" + System.currentTimeMillis());
      }finally {
        Threads.sleep(1000);   
        this.executor.setAllowLocalRequest(false);
      }
    });
  }    
  
  protected abstract void openSigner(String encodedRequest);
}
