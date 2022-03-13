package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Strings.hasText;
import static com.github.utils4j.imp.Strings.trim;
import static com.github.utils4j.imp.Threads.startAsync;
import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.progress4j.IProgressFactory;
import com.github.taskresolver4j.ITaskRequestExecutor;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeCommander;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

abstract class AbstractPjeCommander<I extends IPjeRequest, O extends IPjeResponse> implements IPjeCommander<I, O> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(IPjeCommander.class);

  private final String serverEndpoint;

  protected final IBootable boot;

  protected final ITaskRequestExecutor<IPjeRequest, IPjeResponse> executor;
  
  private final BehaviorSubject<LifeCycle> startup = BehaviorSubject.create();
  
  private boolean started = false;
  
  protected AbstractPjeCommander(IBootable boot, String serverEndpoint) {
    this(boot, serverEndpoint, PjeCertificateAcessor.INSTANCE, PjeSecurityAgent.INSTANCE);
  }
  
  protected AbstractPjeCommander(IBootable boot, String serverEndpoint, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent) {
    this(boot, serverEndpoint, tokenAccess, securityAgent, PjeProgressFactory.DEFAULT);
  }

  protected AbstractPjeCommander(IBootable boot, String serverEndpoint, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent, IProgressFactory factory) {
    this(new PjeTaskRequestExecutor(factory,  tokenAccess, securityAgent), boot, serverEndpoint);
  }
  
  private AbstractPjeCommander(PjeTaskRequestExecutor executor, IBootable boot, String serverEndpoint) {
    this.executor = Args.requireNonNull(executor, "executor is null");
    this.boot = Args.requireNonNull(boot, "boot is null");
    this.serverEndpoint = Args.requireText(serverEndpoint, "serverEndpoint is empty");
  }
  
  @Override
  public final String getServerEndpoint() {
    return serverEndpoint;
  }
  
  protected final String getServerEndpoint(String path) {
    return serverEndpoint + Strings.trim(path);
  }

  private final void notifyShutdown() {
    startup.onNext(LifeCycle.SHUTDOWN);
  }
  
  private final void notifyStartup() {
    startup.onNext(LifeCycle.STARTUP);
  }
  
  private final void notifyKill() {
    startup.onNext(LifeCycle.KILL);
  }
  
  @Override
  public synchronized final boolean isStarted() {
    return started;
  }
  
  @Override
  public final Observable<LifeCycle> lifeCycle() {
    return startup;
  }
  
  public final void exit() {
    boot.exit(1500);
  }
  
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
  public synchronized final void start() throws IOException {
    if (!isStarted()) {
      LOGGER.info("Iniciando " + getClass().getSimpleName());
      this.doStart();
      this.started = true;
      tryRun(this::notifyStartup);
    }
  }
  
  @Override
  public final void stop() throws IOException {
    stop(false);
  }
  
  protected void doStart() throws IOException {}
  
  public synchronized final void stop(boolean kill) throws IOException {
    if (isStarted()) {
      LOGGER.info("Parando " + getClass().getSimpleName());
      this.doStop(kill);
      this.started = false;
      tryRun(executor::close);
      tryRun(this::notifyShutdown);
      if (kill) {
        tryRun(this::notifyKill);
      }
    }
  }
  
  protected void doStop(boolean kill) throws IOException {}
  
  @Override
  public final void execute(String uri) {
    if (!hasText(uri = trim(uri)))
      return;
    final String request = uri;
    startAsync(() -> openRequest(request));
  }    
  
  protected abstract void openRequest(String request);
}