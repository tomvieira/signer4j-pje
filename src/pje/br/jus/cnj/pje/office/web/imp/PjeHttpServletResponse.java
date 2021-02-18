package br.jus.cnj.pje.office.web.imp;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.jus.cnj.pje.office.web.IPjeResponse;

final class PjeHttpServletResponse implements IPjeResponse {

  private final HttpServletResponse response;
  
  public PjeHttpServletResponse(HttpServletResponse response) { 
    this.response = response;
  }
  
  @Override
  public void write(byte[] data) throws IOException {
    response.getOutputStream().write(data);
  }
}

