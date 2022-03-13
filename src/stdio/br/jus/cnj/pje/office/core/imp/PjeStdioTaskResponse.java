package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;
import java.nio.charset.Charset;

import com.github.utils4j.IConstants;

import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeStdioTaskResponse extends PjeStringTaskResponse {

  public PjeStdioTaskResponse(String output) {
    this(output, true);
  }
  
  public PjeStdioTaskResponse(String output, boolean success) {
    this(output, IConstants.DEFAULT_CHARSET, success);
  }

  public PjeStdioTaskResponse(String output, Charset charset) {
    this(output, charset, true);
  }
  
  public PjeStdioTaskResponse(String output, Charset charset, boolean success) {
    super(output, charset, success);
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
    response.write(toBytes(output.length()));
    super.processResponse(response);
  }
  
  private static byte[] toBytes(int length) {
    byte[] bytes = new byte[4];
    bytes[0] = (byte) (length & 0xFF);
    bytes[1] = (byte) ((length >> 8) & 0xFF);
    bytes[2] = (byte) ((length >> 16) & 0xFF);
    bytes[3] = (byte) ((length >> 24) & 0xFF);
    return bytes;
  }
}
