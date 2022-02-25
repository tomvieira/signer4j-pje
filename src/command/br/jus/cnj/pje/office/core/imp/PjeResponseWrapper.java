package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeResponse;

public class PjeResponseWrapper<T extends IPjeResponse> implements IPjeResponse {
  protected final T response;
  
  protected PjeResponseWrapper(T response) {
    this.response = Args.requireNonNull(response, "response is null");
  }

  @Override
  public void write(byte[] data) throws IOException {
    response.write(data);
  }

  @Override
  public void flush() throws IOException {
    response.flush();
  }
}
