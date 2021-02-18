package br.jus.cnj.pje.office.web.imp;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import com.github.signer4j.imp.Constants;
import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.web.IPjeRequest;

@SuppressWarnings("restriction")
final class PjeHttpExchangeRequest implements IPjeRequest {

  private final String userAgent;

  private final List<NameValuePair> queryParams;

  public PjeHttpExchangeRequest(HttpExchange request) { 
    this(request, Constants.DEFAULT_CHARSET);
  }

  public PjeHttpExchangeRequest(HttpExchange request, Charset charset) {
    this.queryParams = parseParams(request.getRequestURI(), charset);
    this.userAgent = request.getRequestHeaders().getFirst(HttpHeaders.USER_AGENT);
  }

  @Override
  public Optional<String> getParameterR() {
    return getParameter(PJE_REQUEST_PARAMETER_NAME);
  }

  @Override
  public Optional<String> getParameterU() {
    return getParameter(PJE_REQUEST_PARAMETER_CACHE);
  }

  @Override
  public Optional<String> getUserAgent() {
    return Optional.ofNullable(userAgent);
  }
  
  private Optional<String> getParameter(String key) {
    return queryParams.stream().filter(n -> key.equalsIgnoreCase(n.getName())).map(n -> n.getValue()).findFirst();
  }
  
  private static List<NameValuePair> parseParams(URI uri, Charset charset) {
    return URLEncodedUtils.parse(uri, charset);
  }
}
