package br.jus.cnj.pje.office.core.imp;

import java.net.URI;
import java.net.URISyntaxException;

public class PjeSysinRequest extends PjeUriRequest {
  public PjeSysinRequest(String uri) throws URISyntaxException {
    super(new URI(uri));
  }
}
