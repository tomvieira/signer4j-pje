package br.jus.cnj.pje.office.core;

import java.io.IOException;

public interface IPjeHttpExchangeResponse extends IPjeResponse {

  void writeHtml(byte[] data) throws IOException;
  
  void writeJson(byte[] data) throws IOException;
}
