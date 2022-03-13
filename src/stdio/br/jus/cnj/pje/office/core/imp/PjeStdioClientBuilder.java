package br.jus.cnj.pje.office.core.imp;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IGetCodec;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeStdioClientBuilder extends PjeTextClientBuilder {

  protected final IGetCodec codec;
  
  PjeStdioClientBuilder(IGetCodec codec) {
    this.codec = Args.requireNonNull(codec, "codec is null");
  }
  
  @Override
  public IPjeClient build() {
    return new PJeStdioClient(Version.current(), charset, codec);
  }
}
