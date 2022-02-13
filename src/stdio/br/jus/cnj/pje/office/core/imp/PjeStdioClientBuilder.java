package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeStdioClientBuilder extends PjeTextClientBuilder {
  
  @Override
  public IPjeClient build() {
    return new PJeStdioClient(Version.current(), charset);
  }
}
