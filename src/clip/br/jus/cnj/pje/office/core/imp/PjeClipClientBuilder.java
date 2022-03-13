package br.jus.cnj.pje.office.core.imp;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IGetCodec;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeClipClientBuilder extends PjeTextClientBuilder {

  private IGetCodec codec;
  
  PjeClipClientBuilder(IGetCodec codec) {
    this.codec = Args.requireNonNull(codec, "codec is null");
  }
  
  @Override
  public IPjeClient build() {
    return new PJeClipClient(Version.current(), charset, codec);
  }
}
