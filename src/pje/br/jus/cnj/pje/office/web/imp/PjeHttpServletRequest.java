package br.jus.cnj.pje.office.web.imp;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.hc.core5.http.HttpHeaders;

import br.jus.cnj.pje.office.web.IPjeRequest;

final class PjeHttpServletRequest implements IPjeRequest {

  private HttpServletRequest request;

  public PjeHttpServletRequest(HttpServletRequest request) { 
    this.request = request;
  }

  @Override
  public Optional<String> getParameterR() {
    return getParameter(PJE_REQUEST_PARAMETER_NAME);
  }

  @Override
  public Optional<String> getParameterU() {
    return getParameter(PJE_REQUEST_PARAMETER_CACHE);
  }

  private Optional<String> getParameter(String key) {
    return Optional.ofNullable(request.getParameter(key));
  }

  @Override
  public Optional<String> getUserAgent() {
    return Optional.ofNullable(request.getHeader(HttpHeaders.USER_AGENT));
  }
}
