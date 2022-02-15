package br.jus.cnj.pje.office.core.imp;


import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.core.IPjeRequestHandler;

@SuppressWarnings("restriction")
abstract class PjeRequestHandler implements IPjeRequestHandler {

  public final void handle(HttpExchange exchange) throws IOException {
    try {
      //Requisições get por imagem não enviam o header origin (aguardando refatoração do lado Javascript). 
      //Neste caso "mocamos" um origin FAKE até que chegue a requisição por POST verdadeira
      process(new PjeHttpExchangeRequestFAKEOrigin(exchange), new PjeHttpExchangeResponse(exchange));
    }finally {
      exchange.close();
    }
  }
  
  protected abstract void process(PjeHttpExchangeRequest request, PjeHttpExchangeResponse response) throws IOException;
}