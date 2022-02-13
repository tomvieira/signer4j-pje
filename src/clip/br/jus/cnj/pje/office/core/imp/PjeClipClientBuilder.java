package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeClipClientBuilder extends PjeTextClientBuilder {

  @Override
  public IPjeClient build() {
    return new PJeClipClient(Version.current(), charset);
  }
}
