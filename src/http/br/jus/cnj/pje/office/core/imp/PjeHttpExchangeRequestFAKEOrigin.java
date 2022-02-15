package br.jus.cnj.pje.office.core.imp;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public final class PjeHttpExchangeRequestFAKEOrigin extends PjeHttpExchangeRequest {
  //Requisições get por imagem não enviam o header origin (aguardando refatoração do lado Javascript). 
  //Neste caso "mocamos" um origin FAKE até que chegue a requisição por POST verdadeira
  public PjeHttpExchangeRequestFAKEOrigin(HttpExchange request) {
    super(request, "https://pje1g.trf3.jus.br/pje"); //FAKE origin for devmode    
  }
}
