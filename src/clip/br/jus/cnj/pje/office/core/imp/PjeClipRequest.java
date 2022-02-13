package br.jus.cnj.pje.office.core.imp;

import java.net.URI;
import java.net.URISyntaxException;

class PjeClipRequest extends PjeUriRequest {
  public PjeClipRequest(String uri) throws URISyntaxException {
    super(new URI(uri), "java.awt.datatransfer.Clipboard");
  }
}
