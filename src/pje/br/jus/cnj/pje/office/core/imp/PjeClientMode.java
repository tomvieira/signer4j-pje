package br.jus.cnj.pje.office.core.imp;

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

import com.github.signer4j.imp.Streams;
import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

public enum PjeClientMode {
  HTTP("http") {
    @Override
    protected PjeClient createClient(PjeClientBuilder builder) {
      return builder.build();
    }
  },
  HTTPS("https") {
    @Override
    protected PjeClient createClient(PjeClientBuilder builder) {
      final KeyStore keyStore;
      try(final InputStream input = PjeClientMode.class.getResourceAsStream("/PjeOffice.jks")) {
        keyStore = KeyStore.getInstance("jks");
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
        return builder.setConnectionManager(cm).build();
      } catch (Exception e) {
        throw new RuntimeException("Impossível instanciar PjeClient em HTTPS", e);
      }
    }
  };
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeClientMode.class);
  
  private PjeClient client;

  private final String name;
  
  private PjeClientMode(String name) {
    this.name = name;
  }
  
  public static IPjeClient clientFrom(String address) {
    return clientFrom(address, ICanceller.NOTHING);
  }
  
  public static IPjeClient clientFrom(String address, ICanceller canceller) {
    return (trim(address).toLowerCase().startsWith(HTTPS.name) ? HTTPS : HTTP).getClient(canceller);
  }
  
  public static void closeClients() {
    HTTP.close();
    LOGGER.info("Cliente HTTP closed");
    HTTPS.close();
    LOGGER.info("Cliente HTTPS closed");
  }
  
  private final IPjeClient getClient(ICanceller canceller) {
    if (client == null) {
      final Timeout _1m = Timeout.ofMinutes(1);
      final Timeout _3m = Timeout.ofMinutes(3);
      final Timeout _30s = Timeout.ofSeconds(30);
      client = createClient(new PjeClientBuilder(Version.current())
          .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
          .evictExpiredConnections()
          .evictIdleConnections(_1m)
          .setDefaultRequestConfig(RequestConfig.custom()
              .setResponseTimeout(_30s)
              .setConnectTimeout(_3m)    
              .setConnectionKeepAlive(_3m)
              .setConnectionRequestTimeout(_3m)
              .setCookieSpec(StandardCookieSpec.IGNORE).build())
          );
    }
    client.setCanceller(canceller);
    return client;
  }
  
  private void close() {
    if (client != null) {
      Streams.closeQuietly(client);
      this.client = null;
    }
  }
  
  protected abstract PjeClient createClient(PjeClientBuilder builder);
  
}
