package br.jus.cnj.pje.office.web;

import java.io.IOException;

public interface IPjeResponse {
  void write(byte[] data) throws IOException;
  void flush() throws IOException;
}
