package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Streams.closeQuietly;
import static com.github.signer4j.imp.Strings.trim;

import java.io.InputStream;
import java.net.ProxySelector;
import java.security.KeyStore;

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

import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeClientBuilder;
import br.jus.cnj.pje.office.core.Version;

public enum PjeClientMode {
  NATIVE("native") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      return new PjeClientExtensionBuilder();
    }
  },
  HTTP("http"),
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
        return ((PjeClientWebBuilder)super.createBuilder()).setConnectionManager(cm);
      } catch (Exception e) {
        throw new RuntimeException("Impossível instanciar PjeClient em HTTPS", e);
      }
    }
  };
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeClientMode.class);
  
  public static void closeClients() {
    for(PjeClientMode mode: values()) {
      mode.close();
      LOGGER.info("client " + mode.name + " closed");
    }
  }
  
  public static IPjeClient clientFrom(String address) {
    return clientFrom(address, ICanceller.NOTHING);
  }
  
  public static IPjeClient clientFrom(String address, ICanceller canceller) {
    String protocol = trim(address).toLowerCase();
    return (protocol.startsWith(NATIVE.name) ? 
      NATIVE : protocol.startsWith(HTTP.name) ? 
      HTTP : 
      HTTPS)
    .getClient(canceller);
  }
  
  private IPjeClient client;

  private final String name;
  
  private PjeClientMode(String name) {
    this.name = name;
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
}
