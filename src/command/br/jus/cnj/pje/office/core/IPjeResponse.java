package br.jus.cnj.pje.office.core;

import java.io.IOException;

public interface IPjeResponse {
  void write(byte[] data) throws IOException;
  void flush() throws IOException;
}
