package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Constants;

import br.jus.cnj.pje.office.core.IPjeClientBuilder;

public abstract class PjeTextClientBuilder implements IPjeClientBuilder {

  protected Charset charset = Constants.DEFAULT_CHARSET;

  public IPjeClientBuilder withCharset(Charset charset) {
    this.charset = Args.requireNonNull(charset, "charset is null");
    return this;
  }
}
