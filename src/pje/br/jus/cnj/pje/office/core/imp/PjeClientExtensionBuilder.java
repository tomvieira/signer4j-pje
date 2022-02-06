package br.jus.cnj.pje.office.core.imp;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeClientBuilder;

class PjeClientExtensionBuilder implements IPjeClientBuilder {

  //TODO we have to go back here!
  
  @Override
  public IPjeClient build() {
    return new PjeExtensionClient();
  }
}
