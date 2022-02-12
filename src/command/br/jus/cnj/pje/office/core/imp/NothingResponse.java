package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import br.jus.cnj.pje.office.core.IPjeResponse;

public enum NothingResponse implements IPjeResponse {
  INSTANCE;
  
  @Override
  public void write(byte[] data) throws IOException {
  }

  @Override
  public void flush() throws IOException {
  }
}
