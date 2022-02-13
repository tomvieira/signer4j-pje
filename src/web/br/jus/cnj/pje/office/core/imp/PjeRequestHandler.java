package br.jus.cnj.pje.office.core.imp;


import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.core.IPjeRequestHandler;

@SuppressWarnings("restriction")
abstract class PjeRequestHandler implements IPjeRequestHandler {

  public final void handle(HttpExchange exchange) throws IOException {
    try {
      process(new PjeHttpExchangeRequest(exchange), new PjeHttpExchangeResponse(exchange));
    }finally {
      exchange.close();
    }
  }
  
  protected abstract void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException;
}