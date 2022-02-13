package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.BackoffManager;
import org.apache.hc.client5.http.classic.ConnectionBackoffStrategy;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.util.TimeValue;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeClientBuilder;
import br.jus.cnj.pje.office.core.Version;

class PjeClientWebBuilder implements IPjeClientBuilder  {

  private HttpClientBuilder clientBuilder = HttpClients.custom();
  
  private final Version version;

  public PjeClientWebBuilder(Version version) {
    this.version = requireNonNull(version, "version is null");
  }
  
  @Override
  public final IPjeClient build() {
    PjeWebClient client = new PjeWebClient(
      clientBuilder.build(),
      version
    );
    return client;    
  }
  
  public final IPjeClientBuilder setRequestExecutor(final HttpRequestExecutor requestExec) {
    clientBuilder.setRequestExecutor(requestExec);
    return this;
  }
  
  public final IPjeClientBuilder setConnectionManager(final HttpClientConnectionManager connManager) {
    this.clientBuilder.setConnectionManager(connManager);
    return this;
  }
  
  public final IPjeClientBuilder setConnectionManagerShared(final boolean shared) {
    clientBuilder.setConnectionManagerShared(false);
    return this;
  }
  
  public final IPjeClientBuilder setConnectionReuseStrategy(final ConnectionReuseStrategy reuseStrategy) {
    clientBuilder.setConnectionReuseStrategy(reuseStrategy);
    return this;
  }
  
  public final IPjeClientBuilder setKeepAliveStrategy(final ConnectionKeepAliveStrategy keepAliveStrategy) {
    clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
    return this;
  }
  
  public final IPjeClientBuilder setTargetAuthenticationStrategy(final AuthenticationStrategy targetAuthStrategy) {
    clientBuilder.setTargetAuthenticationStrategy(targetAuthStrategy);
    return this;
  }
  
  public final IPjeClientBuilder setProxyAuthenticationStrategy(final AuthenticationStrategy proxyAuthStrategy) {
    clientBuilder.setProxyAuthenticationStrategy(proxyAuthStrategy);
    return this;
  }
  
  public final IPjeClientBuilder setUserTokenHandler(final UserTokenHandler userTokenHandler) {
    clientBuilder.setUserTokenHandler(userTokenHandler);
    return this;
  }
  
  public final IPjeClientBuilder disableConnectionState() {
    clientBuilder.disableConnectionState();
    return this;
  }
  
  public final IPjeClientBuilder setSchemePortResolver(final SchemePortResolver schemePortResolver) {
    clientBuilder.setSchemePortResolver(schemePortResolver);
    return this;
  }
  
  public final IPjeClientBuilder setUserAgent(final String userAgent) {
    clientBuilder.setUserAgent(userAgent);
    return this;
  }
  
  public final IPjeClientBuilder setDefaultHeaders(final Collection<? extends Header> defaultHeaders) {
    clientBuilder.setDefaultHeaders(defaultHeaders);
    return this;
  }
  
  public final IPjeClientBuilder addResponseInterceptorFirst(final HttpResponseInterceptor interceptor) {
    clientBuilder.addResponseInterceptorFirst(interceptor);
    return this;
  }
  
  public final IPjeClientBuilder addResponseInterceptorLast(final HttpResponseInterceptor interceptor) {
    clientBuilder.addResponseInterceptorLast(interceptor);
    return this;
  }
  
  public final IPjeClientBuilder addRequestInterceptorFirst(final HttpRequestInterceptor interceptor) {
    clientBuilder.addRequestInterceptorFirst(interceptor);
    return this;
  }
  
  public final IPjeClientBuilder addRequestInterceptorLast(final HttpRequestInterceptor interceptor) {
    clientBuilder.addRequestInterceptorLast(interceptor);
    return this;
  }

  public final IPjeClientBuilder addExecInterceptorBefore(final String existing, final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorBefore(existing, name, interceptor);
    return this;
  }
  
  public final IPjeClientBuilder addExecInterceptorAfter(final String existing, final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorAfter(existing, name, interceptor);
    return this;
  }
  
  public final IPjeClientBuilder replaceExecInterceptor(final String existing, final ExecChainHandler interceptor) {
    clientBuilder.replaceExecInterceptor(existing, interceptor);
    return this;
  }

  public final IPjeClientBuilder addExecInterceptorFirst(final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorFirst(name, interceptor);
    return this;
  }
  
  public final IPjeClientBuilder addExecInterceptorLast(final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorLast(name, interceptor);
    return this;
  }
  
  public final IPjeClientBuilder disableCookieManagement() {
    clientBuilder.disableCookieManagement();
    return this;
  }
  
  public final IPjeClientBuilder disableContentCompression() {
    clientBuilder.disableContentCompression();
    return this;
  }
  
  public final IPjeClientBuilder disableAuthCaching() {
    clientBuilder.disableAuthCaching();
    return this;
  }
  
  public final IPjeClientBuilder setRetryStrategy(final HttpRequestRetryStrategy retryStrategy) {
    clientBuilder.setRetryStrategy(retryStrategy);
    return this;
  }
  
  public final IPjeClientBuilder disableAutomaticRetries() {
    clientBuilder.disableAutomaticRetries();
    return this;
  }
  
  public final IPjeClientBuilder setProxy(final HttpHost proxy) {
    clientBuilder.setProxy(proxy);
    return this;
  }
  
  public final PjeClientWebBuilder setRoutePlanner(final HttpRoutePlanner routePlanner) {
    clientBuilder.setRoutePlanner(routePlanner);
    return this;
  }

  public final IPjeClientBuilder setRedirectStrategy(final RedirectStrategy redirectStrategy) {
    clientBuilder.setRedirectStrategy(redirectStrategy);
    return this;
  }
  
  public final IPjeClientBuilder disableRedirectHandling() {
    clientBuilder.disableRedirectHandling();
    return this;
  }
  
  public final IPjeClientBuilder setConnectionBackoffStrategy(
      final ConnectionBackoffStrategy connectionBackoffStrategy) {
    clientBuilder.setConnectionBackoffStrategy(connectionBackoffStrategy);
    return this;
  }
  
  public final IPjeClientBuilder setBackoffManager(final BackoffManager backoffManager) {
    clientBuilder.setBackoffManager(backoffManager);
    return this;
  }
  
  public final IPjeClientBuilder setDefaultCookieStore(final CookieStore cookieStore) {
    clientBuilder.setDefaultCookieStore(cookieStore);
    return this;
  }
  
  public final IPjeClientBuilder setDefaultCredentialsProvider(
      final CredentialsProvider credentialsProvider) {
    clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    return this;
  }
  
  public final IPjeClientBuilder setDefaultAuthSchemeRegistry(
      final Lookup<AuthSchemeFactory> authSchemeRegistry) {
    clientBuilder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
    return this;
  }

  public final IPjeClientBuilder setDefaultCookieSpecRegistry(
      final Lookup<CookieSpecFactory> cookieSpecRegistry) {
    clientBuilder.setDefaultCookieSpecRegistry(cookieSpecRegistry);
    return this;
  }
  
  public final IPjeClientBuilder setContentDecoderRegistry(
      final LinkedHashMap<String, InputStreamFactory> contentDecoderMap) {
    clientBuilder.setContentDecoderRegistry(contentDecoderMap);
    return this;
  }
  
  public final PjeClientWebBuilder setDefaultRequestConfig(final RequestConfig config) {
    clientBuilder.setDefaultRequestConfig(config);
    return this;
  }
  
  public final IPjeClientBuilder useSystemProperties() {
    clientBuilder.useSystemProperties();
    return this;
  }
  
  public final PjeClientWebBuilder evictExpiredConnections() {
    clientBuilder.evictExpiredConnections();
    return this;
  }

  public final PjeClientWebBuilder evictIdleConnections(final TimeValue maxIdleTime) {
    clientBuilder.evictIdleConnections(maxIdleTime);
    return this;
  }

  public final IPjeClientBuilder disableDefaultUserAgent() {
    clientBuilder.disableDefaultUserAgent();
    return this;
  }
}
