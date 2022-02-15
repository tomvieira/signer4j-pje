package br.jus.cnj.pje.office.core.imp;

import org.apache.hc.core5.http.HttpHeaders;

import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.core.IPjeHeaders;

@SuppressWarnings("restriction")
public class PjeHttpExchangeRequest extends PjeUriRequest {
  public PjeHttpExchangeRequest(HttpExchange request) {
    super(
      request.getRequestURI(), 
      request.getRequestHeaders().getFirst(HttpHeaders.USER_AGENT),
      request.getRequestHeaders().getFirst(IPjeHeaders.ORIGIN)
    );
  }
  
  //constructor for FakeOrigin development mode
  protected PjeHttpExchangeRequest(HttpExchange request, String origin) {
    super(
      request.getRequestURI(), 
      request.getRequestHeaders().getFirst(HttpHeaders.USER_AGENT),
      origin
    );
  }
}
