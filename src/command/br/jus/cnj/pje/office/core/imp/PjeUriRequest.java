package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Strings.trim;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import br.jus.cnj.pje.office.core.IPjeRequest;

public abstract class PjeUriRequest implements IPjeRequest {

  private final URI uri;
  
  private final String userAgent;

  private final List<NameValuePair> queryParams;
  
  private final Optional<String> origin;
  
  protected PjeUriRequest(URI input, String userAgent, String origin) {
    this.queryParams = parseParams(this.uri = input);
    this.userAgent = trim(userAgent, "Unknown");
    this.origin = Optional.of(trim(origin));
  }

  @Override
  public final Optional<String> getParameterR() {
    return getParameter(PJE_REQUEST_PARAMETER_NAME);
  }

  @Override
  public final Optional<String> getParameterU() {
    return getParameter(PJE_REQUEST_PARAMETER_CACHE);
  }
  
  @Override
  public final Optional<String> getUserAgent() {
    return Optional.ofNullable(userAgent);
  }
  
  @Override
  public final Optional<String> getOrigin() {
    return origin;
  }
  
  @Override
  public final String getId() {
    return uri.toString();
  }
  
  @Override
  public final String toString() {
    return getId();
  }
  
  private Optional<String> getParameter(String key) {
    return queryParams.stream().filter(n -> n.getName().equalsIgnoreCase(key)).map(n -> n.getValue()).findFirst();
  }
  
  private static List<NameValuePair> parseParams(URI uri) {
    return new URIBuilder(uri).getQueryParams();
  }  
}
