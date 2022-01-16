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

import br.jus.cnj.pje.office.core.Version;

class PjeClientBuilder  {

  private HttpClientBuilder clientBuilder = HttpClients.custom();
  
  private final Version version;
  

  public PjeClientBuilder(Version version) {
    this.version = requireNonNull(version, "version is null");
  }
  
  public final PjeClient build() {
    PjeClient client = new PjeClient(
      clientBuilder.build(), 
      version
    );
    return client;    
  }
  
  public final PjeClientBuilder setRequestExecutor(final HttpRequestExecutor requestExec) {
    clientBuilder.setRequestExecutor(requestExec);
    return this;
  }
  
  public final PjeClientBuilder setConnectionManager(final HttpClientConnectionManager connManager) {
    this.clientBuilder.setConnectionManager(connManager);
    return this;
  }
  
  public final PjeClientBuilder setConnectionManagerShared(
      final boolean shared) {
    clientBuilder.setConnectionManagerShared(false);
    return this;
  }
  
  public final PjeClientBuilder setConnectionReuseStrategy(
      final ConnectionReuseStrategy reuseStrategy) {
    clientBuilder.setConnectionReuseStrategy(reuseStrategy);
    return this;
  }
  
  public final PjeClientBuilder setKeepAliveStrategy(
      final ConnectionKeepAliveStrategy keepAliveStrategy) {
    clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
    return this;
  }
  
  public final PjeClientBuilder setTargetAuthenticationStrategy(
      final AuthenticationStrategy targetAuthStrategy) {
    clientBuilder.setTargetAuthenticationStrategy(targetAuthStrategy);
    return this;
  }
  
  public final PjeClientBuilder setProxyAuthenticationStrategy(
      final AuthenticationStrategy proxyAuthStrategy) {
    clientBuilder.setProxyAuthenticationStrategy(proxyAuthStrategy);
    return this;
  }
  
  public final PjeClientBuilder setUserTokenHandler(final UserTokenHandler userTokenHandler) {
    clientBuilder.setUserTokenHandler(userTokenHandler);
    return this;
  }
  
  public final PjeClientBuilder disableConnectionState() {
    clientBuilder.disableConnectionState();
    return this;
  }
  
  public final PjeClientBuilder setSchemePortResolver(
      final SchemePortResolver schemePortResolver) {
    clientBuilder.setSchemePortResolver(schemePortResolver);
    return this;
  }
  
  public final PjeClientBuilder setUserAgent(final String userAgent) {
    clientBuilder.setUserAgent(userAgent);
    return this;
  }
  
  public final PjeClientBuilder setDefaultHeaders(final Collection<? extends Header> defaultHeaders) {
    clientBuilder.setDefaultHeaders(defaultHeaders);
    return this;
  }
  
  public final PjeClientBuilder addResponseInterceptorFirst(final HttpResponseInterceptor interceptor) {
    clientBuilder.addResponseInterceptorFirst(interceptor);
    return this;
  }
  
  public final PjeClientBuilder addResponseInterceptorLast(final HttpResponseInterceptor interceptor) {
    clientBuilder.addResponseInterceptorLast(interceptor);
    return this;
  }
  
  public final PjeClientBuilder addRequestInterceptorFirst(final HttpRequestInterceptor interceptor) {
    clientBuilder.addRequestInterceptorFirst(interceptor);
    return this;
  }
  
  public final PjeClientBuilder addRequestInterceptorLast(final HttpRequestInterceptor interceptor) {
    clientBuilder.addRequestInterceptorLast(interceptor);
    return this;
  }

  public final PjeClientBuilder addExecInterceptorBefore(final String existing, final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorBefore(existing, name, interceptor);
    return this;
  }
  
  public final PjeClientBuilder addExecInterceptorAfter(final String existing, final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorAfter(existing, name, interceptor);
    return this;
  }
  
  public final PjeClientBuilder replaceExecInterceptor(final String existing, final ExecChainHandler interceptor) {
    clientBuilder.replaceExecInterceptor(existing, interceptor);
    return this;
  }

  public final PjeClientBuilder addExecInterceptorFirst(final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorFirst(name, interceptor);
    return this;
  }
  
  public final PjeClientBuilder addExecInterceptorLast(final String name, final ExecChainHandler interceptor) {
    clientBuilder.addExecInterceptorLast(name, interceptor);
    return this;
  }
  
  public final PjeClientBuilder disableCookieManagement() {
    clientBuilder.disableCookieManagement();
    return this;
  }
  
  public final PjeClientBuilder disableContentCompression() {
    clientBuilder.disableContentCompression();
    return this;
  }
  
  public final PjeClientBuilder disableAuthCaching() {
    clientBuilder.disableAuthCaching();
    return this;
  }
  
  public final PjeClientBuilder setRetryStrategy(final HttpRequestRetryStrategy retryStrategy) {
    clientBuilder.setRetryStrategy(retryStrategy);
    return this;
  }
  
  public final PjeClientBuilder disableAutomaticRetries() {
    clientBuilder.disableAutomaticRetries();
    return this;
  }
  
  public final PjeClientBuilder setProxy(final HttpHost proxy) {
    clientBuilder.setProxy(proxy);
    return this;
  }
  
  public final PjeClientBuilder setRoutePlanner(final HttpRoutePlanner routePlanner) {
    clientBuilder.setRoutePlanner(routePlanner);
    return this;
  }

  public final PjeClientBuilder setRedirectStrategy(final RedirectStrategy redirectStrategy) {
    clientBuilder.setRedirectStrategy(redirectStrategy);
    return this;
  }
  
  public final PjeClientBuilder disableRedirectHandling() {
    clientBuilder.disableRedirectHandling();
    return this;
  }
  
  public final PjeClientBuilder setConnectionBackoffStrategy(
      final ConnectionBackoffStrategy connectionBackoffStrategy) {
    clientBuilder.setConnectionBackoffStrategy(connectionBackoffStrategy);
    return this;
  }
  
  public final PjeClientBuilder setBackoffManager(final BackoffManager backoffManager) {
    clientBuilder.setBackoffManager(backoffManager);
    return this;
  }
  
  public final PjeClientBuilder setDefaultCookieStore(final CookieStore cookieStore) {
    clientBuilder.setDefaultCookieStore(cookieStore);
    return this;
  }
  
  public final PjeClientBuilder setDefaultCredentialsProvider(
      final CredentialsProvider credentialsProvider) {
    clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    return this;
  }
  
  public final PjeClientBuilder setDefaultAuthSchemeRegistry(
      final Lookup<AuthSchemeFactory> authSchemeRegistry) {
    clientBuilder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
    return this;
  }

  public final PjeClientBuilder setDefaultCookieSpecRegistry(
      final Lookup<CookieSpecFactory> cookieSpecRegistry) {
    clientBuilder.setDefaultCookieSpecRegistry(cookieSpecRegistry);
    return this;
  }
  
  public final PjeClientBuilder setContentDecoderRegistry(
      final LinkedHashMap<String, InputStreamFactory> contentDecoderMap) {
    clientBuilder.setContentDecoderRegistry(contentDecoderMap);
    return this;
  }
  
  public final PjeClientBuilder setDefaultRequestConfig(final RequestConfig config) {
    clientBuilder.setDefaultRequestConfig(config);
    return this;
  }
  
  public final PjeClientBuilder useSystemProperties() {
    clientBuilder.useSystemProperties();
    return this;
  }
  
  public final PjeClientBuilder evictExpiredConnections() {
    clientBuilder.evictExpiredConnections();
    return this;
  }

  public final PjeClientBuilder evictIdleConnections(final TimeValue maxIdleTime) {
    clientBuilder.evictIdleConnections(maxIdleTime);
    return this;
  }

  public final PjeClientBuilder disableDefaultUserAgent() {
    clientBuilder.disableDefaultUserAgent();
    return this;
  }
}
