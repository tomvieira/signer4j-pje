package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;
import java.nio.charset.Charset;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeStringTaskResponse extends PjeTaskResponse {

  protected final String output;
  protected final Charset charset;
  
  public PjeStringTaskResponse(String output, Charset charset) {
    this(output, charset, true);
  }
  
  public PjeStringTaskResponse(String output, Charset charset, boolean success) {
    super(success);
    this.output = Args.requireNonNull(output, "output is null");
    this.charset = Args.requireNonNull(charset, "charset is null");
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
    response.write(output.getBytes(charset));
    response.flush();
  }
}
