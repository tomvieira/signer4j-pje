package br.jus.cnj.pje.office.core.imp;

import java.net.URI;
import java.net.URISyntaxException;

class PjeFileWatchRequest extends PjeUriRequest {
  
  public PjeFileWatchRequest(String uri, String origin) throws URISyntaxException {
    super(new URI(uri), "java.nio.file.WatchService", origin);
  }
}
