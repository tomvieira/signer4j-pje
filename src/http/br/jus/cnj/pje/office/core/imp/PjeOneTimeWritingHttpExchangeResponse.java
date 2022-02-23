package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;

import br.jus.cnj.pje.office.core.IPjeHttpExchangeResponse;

public class PjeOneTimeWritingHttpExchangeResponse extends PjeOneTimeWritingResponse<IPjeHttpExchangeResponse> implements IPjeHttpExchangeResponse{

  protected PjeOneTimeWritingHttpExchangeResponse(IPjeHttpExchangeResponse response) {
    super(response);
  }

  @Override
  public void writeHtml(byte[] data) throws IOException {
    checkAndRun(() -> response.writeHtml(data));
  }

  @Override
  public void writeJson(byte[] data) throws IOException {
    checkAndRun(() -> response.writeJson(data));
  }

}
