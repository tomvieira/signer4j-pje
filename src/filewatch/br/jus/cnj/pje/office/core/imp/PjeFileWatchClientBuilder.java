package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeFileWatchClientBuilder extends PjeStdioClientBuilder {
  
  @Override
  public IPjeClient build() {
    return new PJeFileWatchClient(Version.current(), charset);
  }
}
