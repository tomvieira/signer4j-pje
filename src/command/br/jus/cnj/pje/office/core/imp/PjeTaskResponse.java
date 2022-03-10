package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import com.github.taskresolver4j.ITaskResponse;

import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeTaskResponse implements ITaskResponse<IPjeResponse> {
  public static final PjeTaskResponse NOTHING_SUCCESS = new PjeTaskResponse(true) {};
  public static final PjeTaskResponse NOTHING_FAIL = new PjeTaskResponse(false) {};
  
  private boolean success;
  
  protected PjeTaskResponse(boolean success) {
    this.success = success;
  }
  
  @Override
  public final boolean isSuccess() {
    return success;
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
  }
}
