package br.jus.cnj.pje.office.web.imp;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.web.IPjeRequest;

@SuppressWarnings("restriction")
public final class PjeHttpExchangeRequest implements IPjeRequest {

  private final String userAgent;

  private final List<NameValuePair> queryParams;

  public PjeHttpExchangeRequest(HttpExchange request) {
    this.queryParams = parseParams(request.getRequestURI());
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
    return queryParams.stream().filter(n -> n.getName().equalsIgnoreCase(key)).map(n -> n.getValue()).findFirst();
  }
  
  private static List<NameValuePair> parseParams(URI uri) {
    return new URIBuilder(uri).getQueryParams();
  }
}
