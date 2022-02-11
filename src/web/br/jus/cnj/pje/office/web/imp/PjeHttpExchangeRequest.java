package br.jus.cnj.pje.office.web.imp;

import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.core.imp.PjeUriRequest;

@SuppressWarnings("restriction")
public final class PjeHttpExchangeRequest extends PjeUriRequest {
  public PjeHttpExchangeRequest(HttpExchange request) {
    super(request.getRequestURI());
  }
}
