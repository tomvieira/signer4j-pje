package br.jus.cnj.pje.office.web.imp;


import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.HttpTools;
import com.github.signer4j.imp.Threads;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import br.jus.cnj.pje.office.web.IPjeRequestHandler;
import br.jus.cnj.pje.office.web.IPjeWebServer;

@SuppressWarnings("restriction")
enum PjeServerMode {
  HTTP() {
    @SuppressWarnings("unchecked")
    @Override
    protected HttpServer setup(IWebServerSetup setup) throws IOException {
      HttpServer server = HttpServer.create();
      bind(server, setup);
      return server;
    }
  },
  
  HTTPS() {
    @SuppressWarnings("unchecked")
    @Override
    protected HttpsServer setup(IWebServerSetup setup) throws IOException {
      HttpsServer server = HttpsServer.create();
      bind(server, setup);
      return setupSSL(server);
    }

    HttpsServer setupSSL(final HttpsServer server) throws IOException {
      try {
        //TODO revisar e testar!
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        final char[] password = "pjeoffice".toCharArray();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try(InputStream input = getClass().getResourceAsStream("/PJeOfficeSSL.jks")){
          keyStore.load(input, password);
          KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
          keyManagerFactory.init(keyStore, password);
          sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
          server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
              SSLContext sslContext;
              try {
                sslContext = SSLContext.getDefault();
              } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Incapaz de ler o contexto default SSLContext", e);
              }
              params.setNeedClientAuth(false);
              SSLEngine engine = sslContext.createSSLEngine();
              params.setCipherSuites(engine.getEnabledCipherSuites());
              params.setProtocols(engine.getEnabledProtocols());
              params.setSSLParameters(sslContext.getDefaultSSLParameters());
            }
          });
        }
        return server;
      }catch(Exception e) {
        throw new IOException("Não foi possível configurar SSL no servidor web", e);
      }
    }
  };  
  
  private static Logger LOGGER = LoggerFactory.getLogger(PjeServerMode.class);
  
  protected static void bind(HttpServer server, IWebServerSetup setup) {
    server.setExecutor(setup.getExecutor());
    for(IPjeRequestHandler handler: setup.getHandlers()) {
      HttpContext context = server.createContext(handler.getEndPoint(), handler);
      for(Filter filter: setup.getFilters()) {
        context.getFilters().add(filter);
      }
    }
  }
  
  protected static <T extends HttpServer> T bind(T server, int port) throws IOException {
    int attempt = 1;
    IOException exception = null;
    do {
      try {
        server.bind(new InetSocketAddress(port), 0);
        LOGGER.info("Servidor operando na porta {}", port);
        return server;
      } catch (BindException e) {
        exception = e;
        LOGGER.warn("Porta {} já está sendo utilizada (tentativa {})", port, attempt);
        sendShutdownRequest(port);
      } catch (IOException e) {
        LOGGER.error("Não foi possível bindar na porta {}", port);
        exception = e;
      }
      Threads.sleep(3500);
    }while(attempt++ < 3);
    throw exception;
  }

  public static HttpServer newHttp(IWebServerSetup setup) throws IOException {
    return bind(HTTP.setup(setup), setup.getPort());
  }

  public static HttpsServer newHttps(IWebServerSetup setup) throws IOException {
    return bind(HTTPS.setup(setup), setup.getPort());
  }
  
  private static void sendShutdownRequest(int port) {
    LOGGER.info("Tentativa de shutdown da porta {}", port);
    HttpTools.sendGetRequest("http://127.0.0.1:" + port + IPjeWebServer.SHUTDOWN_ENDPOINT);
  }

  protected abstract <T extends HttpServer> T setup(IWebServerSetup setup) throws IOException;
}
