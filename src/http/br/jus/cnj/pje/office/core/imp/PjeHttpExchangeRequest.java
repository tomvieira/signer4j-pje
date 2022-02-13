package br.jus.cnj.pje.office.core.imp;

import org.apache.hc.core5.http.HttpHeaders;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public final class PjeHttpExchangeRequest extends PjeUriRequest {
  public PjeHttpExchangeRequest(HttpExchange request) {
    super(request.getRequestURI(), request.getRequestHeaders().getFirst(HttpHeaders.USER_AGENT));
  }
}
