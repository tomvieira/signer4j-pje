package br.jus.cnj.pje.office.web.imp;

import java.io.IOException;

import com.github.signer4j.imp.Base64;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;

public final class PjeWebTaskResponse extends PjeTaskResponse {
  
  //A .gif file with 1 pixels
  public static final PjeWebTaskResponse SUCCESS = new PjeWebTaskResponse(
    true, "R0lGODlhAQABAPAAAEz/AAAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw=="
  );
  
  //A .png file with 2 pixels
  public static final PjeWebTaskResponse FAIL = new PjeWebTaskResponse(
    false, "iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAABHNCSVQICAgIfAhkiAAAABFJREFUCJlj/M/A8J+BgYEBAA0FAgD+6nhnAAAAAElFTkSuQmCC"
  );

  private byte[] content;
  
  PjeWebTaskResponse(boolean success, String base64Content) {
    super(success);
    this.content = Base64.base64Decode(base64Content);
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
    response.write(content);
  }
}
