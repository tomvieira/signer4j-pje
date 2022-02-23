package br.jus.cnj.pje.office.core.imp;


import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.core.IPjeHttpExchangeRequest;
import br.jus.cnj.pje.office.core.IPjeHttpExchangeResponse;
import br.jus.cnj.pje.office.core.IPjeRequestHandler;

@SuppressWarnings("restriction")
abstract class PjeRequestHandler implements IPjeRequestHandler {

  public final void handle(HttpExchange exchange) throws IOException {
    try {
      process(new PjeHttpExchangeRequest(exchange), new PjeOneTimeWritingHttpExchangeResponse(new PjeHttpExchangeResponse(exchange)));
    }finally {
      exchange.close();
    }
  }
  
  protected abstract void process(IPjeHttpExchangeRequest request, IPjeHttpExchangeResponse response) throws IOException;
}