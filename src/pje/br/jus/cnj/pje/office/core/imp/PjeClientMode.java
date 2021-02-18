package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Strings.trim;

import java.io.InputStream;
import java.net.ProxySelector;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

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

import com.github.signer4j.imp.Streams;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

enum PjeClientMode {
  HTTP() {
    @Override
    protected IPjeClient createClient(PjeClientBuilder builder) {
      return builder.evictExpiredConnections()
        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(StandardCookieSpec.IGNORE).build())
        .build();
    }
  },
  HTTPS {
    @Override
    protected IPjeClient createClient(PjeClientBuilder builder) {
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
        return builder.setConnectionManager(cm)
          .evictExpiredConnections()
          //.disableAutomaticRetries() //TODO estudar a relação deste parâmetro com o uso de Retryable do método PjeClient.send
          .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
          .build();
      } catch (Exception e) {
        throw new RuntimeException("Impossível instanciar PjeClient em HTTPS", e);
      }
    }
  };
  
  private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
      .setResponseTimeout(Timeout.DISABLED) //This is SO Timeout. Default to infinit!
      .setConnectionRequestTimeout(Timeout.of(3, TimeUnit.MINUTES))
      .setConnectTimeout(Timeout.of(3, TimeUnit.MINUTES))
      .setConnectionKeepAlive(Timeout.of(3, TimeUnit.MINUTES)).build();

  private IPjeClient client;

  public static IPjeClient clientFrom(String address) {
    return (trim(address).toUpperCase().startsWith(HTTPS.name()) ? HTTPS : HTTP).getClient();
  }

  public static void closeClients() {
    HTTP.close();
    HTTPS.close();
  }
  
  private final IPjeClient getClient() {
    return client != null ? client : (client = createClient(
      new PjeClientBuilder(Version.current())
        .setDefaultRequestConfig(REQUEST_CONFIG))
    );
  }
  
  private void close() {
    if (client != null) {
      Streams.closeQuietly(client);
      this.client = null;
    }
  }
  
  protected abstract IPjeClient createClient(PjeClientBuilder builder);
  
}
