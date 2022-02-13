package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.signer4j.imp.HttpTools.touchQuietly;
import static com.github.signer4j.imp.SwingTools.invokeLater;
import static com.github.signer4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hc.core5.http.HttpStatus;

import com.github.signer4j.IFinishable;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import br.jus.cnj.pje.office.core.ICorsHeaders;
import br.jus.cnj.pje.office.core.IPjeHeaders;
import br.jus.cnj.pje.office.core.IPjeRequestHandler;
import br.jus.cnj.pje.office.core.IPjeWebServer;
import br.jus.cnj.pje.office.core.Version;

@SuppressWarnings("restriction") 
class PjeWebServer extends PjeCommander<PjeHttpExchangeRequest, PjeHttpExchangeResponse> implements IPjeWebServer {

  private static class AccessFilter extends Filter {
    @Override
    public String description() {
      return "Access filter";
    }

    @Override
    public void doFilter(HttpExchange request, Chain chain) throws IOException {
      InetAddress remote = request.getRemoteAddress().getAddress();
      if (!remote.isLoopbackAddress()) {
        LOGGER.warn("Tentativa de acesso indevido a partir do ip: " + remote.getHostAddress());
        request.sendResponseHeaders(HttpStatus.SC_UNAUTHORIZED, IPjeHeaders.NO_RESPONSE_BODY);
        request.close();
      } else {
        chain.doFilter(request);
      } 
    }
  }
  
  private static class CorsFilter extends Filter {
    @Override
    public String description() {
      return "Cors filter";
    }

    @Override
    public void doFilter(HttpExchange request, Chain chain) throws IOException {
      Headers response = request.getResponseHeaders();
      response.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      response.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, "true");
      response.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS, POST");
      response.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      response.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      response.set(ICorsHeaders.ACCESS_CONTROL_MAX_AGE, "86400"); //one day!
      if ("OPTIONS".equalsIgnoreCase(request.getRequestMethod())) {
        request.sendResponseHeaders(HttpStatus.SC_NO_CONTENT, IPjeHeaders.NO_RESPONSE_BODY);
      } else {
        chain.doFilter(request);
      }
    }
  }
  
  private static class PingRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT;
    }
    
    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      PjeWebTaskResponse.SUCCESS.processResponse(response);
    }
  }
  
  private static class VersionRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "versao/";
    }

    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      response.writeJson(Version.jsonBytes());
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
      PjeWebTaskResponse.SUCCESS.processResponse(response);
      PjeWebServer.this.exit();
    }
  }
  
  private class LogoutRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return LOGOUT_ENDPOINT;
    }
    
    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      LOGGER.info("Recebida requisição de logout do certificado");
      PjeWebTaskResponse.SUCCESS.processResponse(response);
      PjeWebServer.this.logout();
    }    
  } 
  
  private class TaskRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "requisicao/";
    }
    
    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      PjeWebServer.this.execute(request, response);
    }
  }
  
  private class PluginRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "plugin";
    }

    @Override
    protected void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException {
      response.writeHtml(Files.readAllBytes(Paths.get("./plugin.html")));
    }
  }
  
  private HttpServer httpServer;
  private HttpsServer httpsServer;
  private PjeWebServerBuilder setup;
  
  private final AtomicBoolean running = new AtomicBoolean(false);

  private final Filter cors   = new CorsFilter();
  private final Filter access = new AccessFilter();
  
  private final IPjeRequestHandler ping = new PingRequestHandler();
  private final IPjeRequestHandler task = new TaskRequestHandler();
  private final IPjeRequestHandler vers = new VersionRequestHandler();
  private final IPjeRequestHandler exit = new ShutdownRequestHandler();
  private final IPjeRequestHandler vaza = new LogoutRequestHandler();

  PjeWebServer(IFinishable finishingCode) {
    super(finishingCode, "http://127.0.0.1:" + HTTP_PORT);
  }
  
  @Override
  public void execute(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) {
    if (!isStarted())
      throw new IllegalStateException("web server not started!");
    if (!running.getAndSet(true)) {
      try {
        super.execute(request, response);
      }finally {
        running.set(false);
      }
    } else {
      invokeLater(() -> display("Ainda há uma operação em andamento!\nCancele ou aguarde a finalização!"));
      handleException(request, response, null);
    }
  }
  
  @Override
  protected void handleException(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response, Exception e) {
    tryRun(() -> PjeWebTaskResponse.FAIL.processResponse(response));
  }
  
  @Override
  public synchronized boolean isStarted() {
    return setup != null;
  }
  
  private void startHttps() throws IOException {
    if (httpsServer == null) {
      httpsServer = PjeServerMode.newHttps(setup.usingPort(HTTPS_PORT));
      httpsServer.start();
    }
  }

  private void startHttp() throws IOException {
    if (httpServer == null) {
      httpServer = PjeServerMode.newHttp(setup.usingPort(HTTP_PORT));
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
        this.setup = new PjeWebServerBuilder()
          .usingFilter(access)
          .usingFilter(cors)
          .usingHandler(ping)
          .usingHandler(vers)
          .usingHandler(task)
          .usingHandler(exit)
          .usingHandler(vaza).usingHandler(new PluginRequestHandler());
        startHttp();
        startHttps();
      } catch (IOException e) {
        LOGGER.warn("Não foi possível iniciar o servidor", e);
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
      tryRun(setup::shutdown);
      try {
        stopHttp();
        stopHttps();
      }finally {
        super.stop(kill);
        setup = null;
        LOGGER.info("Servidor web parado");
      }
    }
  }

  @Override
  protected void openSigner(String request) {
    touchQuietly(getServerEndpoint(task.getEndPoint()) + request + "&u=" + System.currentTimeMillis());
  }
}

