package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeStdioResponse implements IPjeResponse {

  @Override
  public void write(byte[] data) throws IOException {
    System.out.write(data);
  }

  @Override
  public void flush() throws IOException {
    System.out.flush();
  }
}
