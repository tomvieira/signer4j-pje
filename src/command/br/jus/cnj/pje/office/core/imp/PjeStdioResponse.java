package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.task.ITaskResponse;

import br.jus.cnj.pje.office.web.IPjeResponse;

public class PjeStdioResponse implements ITaskResponse<IPjeResponse>{

  private String output;
  
  public PjeStdioResponse(String output) {
    this.output = Args.requireNonNull(output, "output is null");
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
    response.write(toBytes(output.length()));
    response.write(output.getBytes(Constants.UTF_8));
    response.flush();
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
