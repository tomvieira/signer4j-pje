package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.utils4j.imp.function.Executable;

import br.jus.cnj.pje.office.core.IPjeResponse;

public class PjeOneTimeWritingResponse<T extends IPjeResponse> extends PjeResponseWrapper<T> {

  protected boolean written = false;
  
  protected PjeOneTimeWritingResponse(T response) {
    super(response);
  }

  @Override
  public void write(final byte[] data) throws IOException {
    checkAndRun(() -> super.write(data));
  }
  
  protected void checkAndRun(Executable<IOException> r) throws IOException {
    if (!written) {
      r.execute();
      written = true;
    }
  }
}
