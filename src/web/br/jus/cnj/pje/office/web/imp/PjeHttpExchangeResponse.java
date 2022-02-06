package br.jus.cnj.pje.office.web.imp;

import java.io.IOException;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.web.IPjeResponse;

@SuppressWarnings("restriction")
public final class PjeHttpExchangeResponse implements IPjeResponse {

  private final HttpExchange response;
  
  public PjeHttpExchangeResponse(HttpExchange response) { 
    this.response = response;
  }

  @Override
  public void write(byte[] data) throws IOException {
    response.sendResponseHeaders(HttpStatus.SC_SUCCESS, data.length);
    response.getResponseBody().write(data);
    response.getResponseBody().flush();
  }
  
  public void writeJson(byte[] data) throws IOException {
    Headers headers = response.getResponseHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    write(data);
  }
}
