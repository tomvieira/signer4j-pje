package br.jus.cnj.pje.office.web.imp;

import static br.jus.cnj.pje.office.gui.alert.MessageAlert.display;
import static com.github.signer4j.imp.SwingTools.invokeLater;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.HttpTools;
import com.github.signer4j.imp.Throwables;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.task.ITaskRequestExecutor;
import com.github.signer4j.task.exception.TaskExecutorException;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import br.jus.cnj.pje.office.core.IExitable;
import br.jus.cnj.pje.office.core.IPjeProgressView;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.core.ISecurityAgent;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.core.imp.PjeResponse;
import br.jus.cnj.pje.office.gui.PjeProgressView;
import br.jus.cnj.pje.office.web.IPjeRequest;
import br.jus.cnj.pje.office.web.IPjeRequestHandler;
import br.jus.cnj.pje.office.web.IPjeResponse;
import br.jus.cnj.pje.office.web.IPjeWebServer;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@SuppressWarnings({ "restriction"})
public class PjeWebServer implements IPjeWebServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(PjeWebServer.class);

  private class AccessFilter extends Filter {
    @Override
    public String description() {
      return "Access filter";
    }

    @Override
    public void doFilter(HttpExchange request, Chain chain) throws IOException {
      String remote = request.getRemoteAddress().getAddress().getHostAddress().trim();
      if (!HttpTools.isLocalHost(remote)) {
        String local = request.getLocalAddress().getAddress().getHostAddress().trim();
        String message = "Identificado acesso indevido ao seu PjeOffice.\n" +
         "IP remoto: " + remote + "\n" +
         "IP local: " + local + "\n" +
         "Por favor, remova seu certificado do computador\n" +
         "e notifique este alerta ao suporte em segurança!";
        LOGGER.warn(message);
        invokeLater(() -> display(message));
        request.sendResponseHeaders(HttpStatus.SC_UNAUTHORIZED, 0);
        request.close();
      } else {
        chain.doFilter(request);
      } 
    }
  }
  
  private class CorsFilter extends Filter {
    @Override
    public String description() {
      return "Cors filter";
    }

    @Override
    public void doFilter(HttpExchange request, Chain chain) throws IOException {
      Headers response = request.getResponseHeaders();
      response.set("Access-Control-Allow-Origin", "*");
      response.set("Access-Control-Allow-Credentials", "true");
      response.set("Access-Control-Allow-Methods", "GET");
      response.set("Access-Control-Max-Age", "86400"); //one day!
      response.set("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept");
      chain.doFilter(request);
    }
  }
  
  private class PingRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT;
    }
    
    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      LOGGER.debug("Recebido pedido de ping");
      PjeResponse.SUCCESS.processResponse(response);
    }
  }
  
  private class VersionRequestHandler extends PjeRequestHandler {
    
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "versao/";
    }

    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      LOGGER.debug("Recebido pedido de versão");
      response.writeJson(Version.jsonBytes());
    }
  }
  
  private class TaskRequestHandler extends PjeRequestHandler {
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "requisicao/";
    }
    
    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      if (!running.getAndSet(true)) {
        try {
          PjeWebServer.this.executor.execute(request, response);
        } catch (TaskExecutorException e) {
          LOGGER.error("Exceção no ciclo de vida da requisição", e);
          PjeResponse.FAIL.processResponse(response);
        }finally {
          running.set(false);
        }
      } else {
        invokeLater(() -> display("Ainda há uma operação em andamento!\nCancele ou aguarde a finalização!"));
        PjeResponse.FAIL.processResponse(response);
      }
    }
  }
  
  private class ShutdownRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return SHUTDOWN_ENDPOINT;
    }
    
    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      LOGGER.info("Recebida requisição de parada do servidor");
      PjeResponse.SUCCESS.processResponse(response);
      PjeWebServer.this.exit();
    }
  }
  
  private final Filter access = new AccessFilter();
  private final Filter cors   = new CorsFilter();
  
  private final IPjeRequestHandler ping = new PingRequestHandler();
  private final IPjeRequestHandler task = new TaskRequestHandler();
  private final IPjeRequestHandler vers = new VersionRequestHandler();
  private final IPjeRequestHandler exit = new ShutdownRequestHandler();

  private final ITaskRequestExecutor<IPjeRequest, IPjeResponse> executor;
  
  private final BehaviorSubject<LifeCycle> startup = BehaviorSubject.create();

  private HttpServer httpServer;
  private HttpsServer httpsServer;
  private PjeWebServerSetup setup;
  private IExitable exitable;
  
  private final AtomicBoolean localRequest = new AtomicBoolean(false);
  
  @Override
  public void setAllowLocalRequest(boolean enabled) {
    this.localRequest.set(enabled);
  }
  
  public PjeWebServer(IPjeTokenAccess tokenAccess, ISecurityAgent securityAgent, IExitable exitable) {
    this(PjeProgressView.INSTANCE, PjeProgressView.INSTANCE.get(), tokenAccess, securityAgent, exitable);
  }

  public PjeWebServer(IPjeProgressView view, IProgressFactory factory, IPjeTokenAccess tokenAccess, ISecurityAgent securityAgent, IExitable exitable) {
    this.executor = new PjeTaskRequestExecutor(view, factory, tokenAccess, securityAgent, localRequest);
    this.exitable = exitable;
  }
  
  @Override
  public String getTaskEndpoint() {
    return task.getEndPoint();
  }
  
  private void notifyShutdown() {
    startup.onNext(LifeCycle.SHUTDOWN);
  }
  
  private void notifyStartup() {
    startup.onNext(LifeCycle.STARTUP);
  }
  
  private void notifyKill() {
    startup.onNext(LifeCycle.KILL);
  }
  
  @Override
  public Observable<LifeCycle> lifeCycle() {
    return startup;
  }

  @Override
  public synchronized boolean isStarted() {
    return setup != null;
  }
  
  private void exit() {
    exitable.exit(1500);
  }
  
  private void startHttps() throws IOException {
    if (httpsServer == null) {
      httpsServer = PjeServerMode.newHttps(setup.usingPort(8801));
      httpsServer.start();
    }
  }

  private void startHttp() throws IOException {
    if (httpServer == null) {
      httpServer = PjeServerMode.newHttp(setup.usingPort(8800));
      httpServer.start();
    }
  }
  
  private void stopHttps() {
    if (httpsServer != null) {
      httpsServer.stop(0);
      httpsServer = null;
    }
  }

  private void stopHttp() {
    if (httpServer != null) {
      httpServer.stop(0);
      httpServer = null;
    }
  }
  
  @Override
  public synchronized void start() throws IOException {
    if (!isStarted()) {
      LOGGER.info("Iniciando servidor PjeWebServer");
      try {
        this.setup = new PjeWebServerSetup()
          .usingFilter(access)
          .usingFilter(cors)
          .usingHandler(ping)
          .usingHandler(vers)
          .usingHandler(task)
          .usingHandler(exit);
        
        startHttp();
        startHttps();
      } catch (IOException e) {
        LOGGER.warn("Não fio possível iniciar o servidor", e);
        stop(false);
        throw e;
      }
      notifyStartup();
    }
  }
  
  @Override
  public synchronized void stop(boolean kill) {
    if (isStarted()) {
      LOGGER.info("Parando servidor PjeWebServer");
      Throwables.tryRun(setup::shutdown);
      try {
        stopHttp();
        stopHttps();
      }finally {
        setup = null;
        LOGGER.info("Servidor web parado");
      }
      notifyShutdown();
      if (kill) {
        notifyKill();
      }
    }
  }
}

