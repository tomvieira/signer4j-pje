package br.jus.cnj.pje.office.core.imp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.github.signer4j.imp.Throwables;

import br.jus.cnj.pje.office.web.IPjeRequest;

public class PjeSysinRequest implements IPjeRequest {
  
  private final String userAgent;

  private final List<NameValuePair> queryParams;
  
  public PjeSysinRequest(String input) {
    this.queryParams = parseParams(input);
    this.userAgent = "stdio";
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
  
  private static List<NameValuePair> parseParams(String uri) {
    return Throwables.tryCall(() -> new URIBuilder(uri).getQueryParams(), Collections.emptyList());
  }
}
