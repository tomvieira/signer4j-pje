package br.jus.cnj.pje.office.core.imp;

import java.net.URI;
import java.net.URISyntaxException;

class PjeStdioRequest extends PjeUriRequest {
  public PjeStdioRequest(String uri, String origin) throws URISyntaxException {
    super(new URI(uri), "java.lang.System.in", origin);
  }
}
