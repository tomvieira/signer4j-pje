package br.jus.cnj.pje.office.core.imp;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.Charset;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Constants;

import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeClipResponse implements IPjeResponse {
  
  private final Charset charset;

  public PjeClipResponse() {
    this(Constants.DEFAULT_CHARSET);
  }
  
  public PjeClipResponse(Charset charset) {
    this.charset = Args.requireNonNull(charset, "charset is null");
  }

  @Override
  public void flush() throws IOException {
    ; //nothing to do (empty statement)
  }
  
  @Override
  public void write(byte[] data) throws IOException {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(new String(data, charset)), null);
  }
}
