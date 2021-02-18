package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.signer4j.imp.Base64;
import com.github.signer4j.task.ITaskResponse;

import br.jus.cnj.pje.office.web.IPjeResponse;

public enum PjeResponse implements ITaskResponse<IPjeResponse> {
  //A .gif file with 1 pixels
  SUCCESS("R0lGODlhAQABAPAAAEz/AAAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw=="), 
  
  //A .png file with 2 pixels
  FAIL("iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAABHNCSVQICAgIfAhkiAAAABFJREFUCJlj/M/A8J+BgYEBAA0FAgD+6nhnAAAAAElFTkSuQmCC");

  private byte[] content;

  PjeResponse(String base64Content) {
    this.content = Base64.base64Decode(base64Content);
  }
  
  public int length() {
    return content.length;
  }

  @Override
  public void processResponse(IPjeResponse response) throws IOException {
    response.write(content);
  }
}
