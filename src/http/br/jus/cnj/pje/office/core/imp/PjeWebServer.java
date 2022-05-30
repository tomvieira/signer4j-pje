/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.HttpTools.touchQuietly;
import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hc.core5.http.HttpStatus;

import com.github.signer4j.gui.alert.MessageAlert;
import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Environment;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.ICorsHeaders;
import br.jus.cnj.pje.office.core.IPjeHeaders;
import br.jus.cnj.pje.office.core.IPjeHttpExchangeRequest;
import br.jus.cnj.pje.office.core.IPjeHttpExchangeResponse;
import br.jus.cnj.pje.office.core.IPjeRequestHandler;
import br.jus.cnj.pje.office.core.IPjeWebServer;
import br.jus.cnj.pje.office.core.Version;

@SuppressWarnings("restriction") 
class PjeWebServer extends AbstractPjeCommander<IPjeHttpExchangeRequest, IPjeHttpExchangeResponse> implements IPjeWebServer {

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
      Headers respHeaders = request.getResponseHeaders();
      respHeaders.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      respHeaders.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, "true");
      respHeaders.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS, POST");
      respHeaders.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      respHeaders.set(ICorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      respHeaders.set(ICorsHeaders.ACCESS_CONTROL_MAX_AGE, "86400"); //one day!
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
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      PjeWebTaskResponse.success(request.isPost()).processResponse(response);
    }
  }
  
  private static class VersionRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "versao/";
    }

    @Override
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      response.writeJson(Version.jsonBytes());
    }
  }
  
  private class ShutdownRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return SHUTDOWN_ENDPOINT;
    }
    
    @Override
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      LOGGER.info("Recebida requisição de parada do servidor");
      PjeWebTaskResponse.success(request.isPost()).processResponse(response);
      PjeWebServer.this.exit();
    }
  }
  
  private class LogoutRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return LOGOUT_ENDPOINT;
    }
    
    @Override
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      LOGGER.info("Recebida requisição de logout do token");
      PjeWebTaskResponse.success(request.isPost()).processResponse(response);
      PjeWebServer.this.logout();
    }    
  } 
  
  private class TaskRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "requisicao/";
    }
    
    @Override
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      PjeWebServer.this.execute(request, response);
    }
  }
  
  private class ApiDemoFrontEndRequestHandler extends PjeRequestHandler {
    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "api";
    }

    @Override
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      Optional<Path> p = Environment.resolveTo("PJEOFFICE_HOME", "web/" + request.getParameter("file").orElse("index.html"), false, true);
      if (p.isPresent()) {
        response.writeFile(p.get().toFile());       
      } else {
        response.notFound();
      }
    }
  }
  
  private class ApiDemoBackEndRequestHandler extends PjeRequestHandler {

    @Override
    public String getEndPoint() {
      return BASE_END_POINT + "pjefake";
    }

    @Override
    protected void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException {
      response.writeHtml("Sucesso".getBytes(IConstants.DEFAULT_CHARSET));      
    }    
  } 
  
  private HttpServer httpServer;
  private HttpsServer httpsServer;
  private PjeWebServerBuilder setup;
  
  private final AtomicBoolean taskRunning = new AtomicBoolean(false);

  private final Filter cors   = new CorsFilter();
  private final Filter access = new AccessFilter();
  
  private final IPjeRequestHandler ping  = new PingRequestHandler();
  private final IPjeRequestHandler task  = new TaskRequestHandler();
  private final IPjeRequestHandler vers  = new VersionRequestHandler();
  private final IPjeRequestHandler exit  = new ShutdownRequestHandler();
  private final IPjeRequestHandler vaza  = new LogoutRequestHandler();
  
  private final IPjeRequestHandler front = new ApiDemoFrontEndRequestHandler();
  private final IPjeRequestHandler back  = new ApiDemoBackEndRequestHandler();

  PjeWebServer(IBootable finishingCode) {
    super(finishingCode, "http://127.0.0.1:" + HTTP_PORT);
  }
  
  @Override
  public void execute(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) {
    if (!isStarted())
      throw new IllegalStateException("web server not started!");
    if (!taskRunning.getAndSet(true)) {
      try {
        super.execute(request, response);
      }finally {
        taskRunning.set(false);
      }
    } else {
      MessageAlert.showInfo("Ainda há uma operação em andamento!\nCancele ou aguarde a finalização!");
      handleException(request, response, null);
    }
  }
  
  @Override
  protected void handleException(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response, Exception e) {
    tryRun(() -> PjeWebTaskResponse.fail(request.isPost()).processResponse(response));
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
  protected void doStart() throws IOException {
    try {
      this.setup = new PjeWebServerBuilder()
        .usingFilter(access)
        .usingFilter(cors)
        .usingHandler(ping)
        .usingHandler(vers)
        .usingHandler(task)
        .usingHandler(exit)
        .usingHandler(vaza)
        .usingHandler(front)
        .usingHandler(back);         
      startHttp();
      startHttps();
    } catch (IOException e) {
      LOGGER.warn("Não foi possível iniciar o servidor", e);
      doStop(false);
      throw e;
    }
    super.doStart();
  }
  
  @Override
  protected void doStop(boolean kill) throws IOException {
    tryRun(setup::shutdown);
    setup = null;
    tryRun(this::stopHttp);
    tryRun(this::stopHttps);
    super.doStop(kill);
  }
  
  @Override
  protected void openRequest(String request) {
    touchQuietly(getServerEndpoint(task.getEndPoint()) + request);
  }
}

