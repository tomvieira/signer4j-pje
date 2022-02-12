package br.jus.cnj.pje.office.core.imp;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.github.signer4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeRequest;

public abstract class PjeUriRequest implements IPjeRequest {

  private final String userAgent;

  private final List<NameValuePair> queryParams;
  
  protected PjeUriRequest(URI input, String userAgent) {
    this.queryParams = parseParams(input);
    this.userAgent = Strings.trim(userAgent, "Unknown");
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
