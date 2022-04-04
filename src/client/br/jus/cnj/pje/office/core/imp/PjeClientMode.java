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

import com.github.utils4j.ICanceller;
import com.github.utils4j.imp.Throwables;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeClientBuilder;
import br.jus.cnj.pje.office.core.Version;

public enum PjeClientMode {
  STDIO("stdio") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      return new PjeStdioClientBuilder(HTTP.getClient().getCodec());
    }

    @Override
    protected Function<String, PjeTaskResponse> success(boolean json) {
      return (o) -> new PjeStdioTaskResponse(o);
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail(boolean json) {
      return (t) -> new PjeStdioTaskResponse(Throwables.rootMessage(t), false);
    }
  },
  FILEWATCH("filewatch") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      return new PjeFileWatchClientBuilder(HTTP.getClient().getCodec());
    }
  },
  CLIP("clip") {
    @Override
    protected IPjeClientBuilder createBuilder() {
      return new PjeClipClientBuilder(HTTP.getClient().getCodec());
    }

    @Override
    protected Function<String, PjeTaskResponse> success(boolean json) {
      return (o) -> new PjeClipTaskResponse(o);
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail(boolean json) {
      return (t) -> new PjeClipTaskResponse(Throwables.rootMessage(t), false);
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
    protected Function< String, PjeTaskResponse> success(boolean json) {
      return (o) -> PjeWebTaskResponse.success(json);
    }

    @Override
    protected Function<Throwable, PjeTaskResponse> fail(boolean json) {
      return (t) -> PjeWebTaskResponse.fail(json);
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
    protected Function<Throwable, PjeTaskResponse> fail(boolean json) {
      return HTTP.fail(json);
    }

    @Override
    protected Function<String, PjeTaskResponse> success(boolean json) {
      return HTTP.success(json);
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
      if (protocol.startsWith(mode.protocol + ":"))
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
  
  public static Function<Throwable, PjeTaskResponse> failFrom(String address, boolean json) {
    return from(trim(address).toLowerCase()).fail(json);
  }
  
  public static Function<String, PjeTaskResponse> successFrom(String address, boolean json) {
    return from(trim(address).toLowerCase()).success(json);
  }
  
  private IPjeClient client;

  private final String protocol;
  
  private PjeClientMode(String name) {
    this.protocol = name;
  }
  
  final String getProtocol() {
    return protocol;
  }

  protected Function<String, PjeTaskResponse> success(boolean json) {
    return (o) -> PjeTaskResponse.NOTHING_SUCCESS;
  }
  
  protected Function<Throwable, PjeTaskResponse> fail(boolean json) {
    return (t) -> PjeTaskResponse.NOTHING_FAIL;
  }
  
  protected final IPjeClient getClient() {
    return getClient(null);
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
