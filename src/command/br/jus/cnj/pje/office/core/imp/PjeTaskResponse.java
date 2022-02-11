package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.signer4j.task.ITaskResponse;

import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeTaskResponse implements ITaskResponse<IPjeResponse> {
  public static final PjeTaskResponse NOTHING = new PjeTaskResponse() {};
  
  @Override
  public boolean isSuccess() {
    return true;
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
  }
}
