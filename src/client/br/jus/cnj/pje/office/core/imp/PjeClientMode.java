package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Streams.closeQuietly;
import static com.github.utils4j.imp.Strings.trim;

import java.io.InputStream;
import java.net.ProxySelector;
import java.security.KeyStore;
import java.util.function.Function;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.progress4j.ICanceller;
import com.github.utils4j.imp.Throwables;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeClientBuilder;
import br.jus.cnj.pje.office.core.Version;

public enum PjeClientMode {
  STDIO("stdio") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      return new PjeStdioClientBuilder();
    }

    @Override
    protected Function<String, PjeTaskResponse> success() {
      return (o) -> new PjeStdioTaskResponse(o);
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail() {
      return (t) -> new PjeStdioTaskResponse(Throwables.rootMessage(t));
    }
  },
  CLIP("clip") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      return new PjeClipClientBuilder();
    }

    @Override
    protected Function<String, PjeTaskResponse> success() {
      return (o) -> new PjeClipTaskResponse(o);
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail() {
      return (t) -> new PjeClipTaskResponse(Throwables.rootMessage(t));
    }
  },
  HTTP("http") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      final Timeout _1m = Timeout.ofMinutes(1);
      final Timeout _3m = Timeout.ofMinutes(3);
      final Timeout _30s = Timeout.ofSeconds(30);
      return new PjeClientWebBuilder(Version.current())
        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        .evictExpiredConnections()
        .evictIdleConnections(_1m)
        .setDefaultRequestConfig(RequestConfig.custom()
          .setResponseTimeout(_30s)
          .setConnectTimeout(_3m)    
          .setConnectionKeepAlive(_3m)
          .setConnectionRequestTimeout(_3m)
          .setCookieSpec(StandardCookieSpec.IGNORE).build()
        );
    }

    @Override
    protected Function< String, PjeTaskResponse> success() {
      return (o) -> PjeWebTaskResponse.SUCCESS;
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail() {
      return (t) -> PjeWebTaskResponse.FAIL;
    }
  },
  HTTPS("https") {
    @Override
    protected IPjeClientBuilder createBuilder(){
      try(final InputStream input = PjeClientMode.class.getResourceAsStream("/PjeOffice.jks")) {
        final KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(input, "pjeoffice".toCharArray());
        final SSLContext sslcontext = SSLContexts.custom()
          .loadTrustMaterial(keyStore, new TrustAllStrategy()) //TODO we need to review this trust strategy!
          .build();
        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
          .setSslContext(sslcontext)
          .build();
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
          .setSSLSocketFactory(sslSocketFactory)
          .build();
        final PjeClientWebBuilder builder = (PjeClientWebBuilder)HTTP.createBuilder();
        return builder.setConnectionManager(cm);
      } catch (Exception e) {
        throw new RuntimeException("Impossível instanciar PjeClient em HTTPS", e);
      }
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail() {
      return HTTP.fail();
    }

    @Override
    protected Function<String, PjeTaskResponse> success() {
      return HTTP.success();
    }
  },
  NOTHING("nothing") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      throw new RuntimeException("Unsupported client for 'nothing' protocol ");
    }
  };
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeClientMode.class);
  
  private static PjeClientMode from(String protocol) {
    for(PjeClientMode mode: values()) {
      if (protocol.startsWith(mode.protocol))
        return mode;
    }
    throw new RuntimeException("Unrecognized protocol " + protocol);
  }
  
  public static void closeClients() {
    for(PjeClientMode mode: values()) {
      mode.close();
      LOGGER.info("client " + mode.protocol + " closed");
    }
  }
  
  public static IPjeClient clientFrom(String address) {
    return clientFrom(address, ICanceller.NOTHING);
  }
  
  public static IPjeClient clientFrom(String address, ICanceller canceller) {
    return from(trim(address).toLowerCase()).getClient(canceller);
  }
  
  public static Function<Throwable, PjeTaskResponse> failFrom(String address) {
    return from(trim(address).toLowerCase()).fail();
  }
  
  public static Function<String, PjeTaskResponse> successFrom(String address) {
    return from(trim(address).toLowerCase()).success();
  }
  
  private IPjeClient client;

  private final String protocol;
  
  private PjeClientMode(String name) {
    this.protocol = name;
  }
  
  final String getProtocol() {
    return protocol;
  }

  protected Function<String, PjeTaskResponse> success() {
    return (o) -> PjeTaskResponse.NOTHING;
  }
  
  protected Function<Throwable, PjeTaskResponse> fail() {
    return (t) -> PjeTaskResponse.NOTHING;
  }
    
  protected final IPjeClient getClient(ICanceller canceller) {
    if (client == null) {
      client = createBuilder().build();
    }
    client.setCanceller(canceller);
    return client;
  }
  
  private void close() {
    if (client != null) {
      closeQuietly(client);
      client = null;
    }
  }

  protected abstract IPjeClientBuilder createBuilder();

}
