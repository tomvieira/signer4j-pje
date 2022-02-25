package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeClientBuilder;

public abstract class PjeTextClientBuilder implements IPjeClientBuilder {

  protected Charset charset = IConstants.DEFAULT_CHARSET;

  public IPjeClientBuilder withCharset(Charset charset) {
    this.charset = Args.requireNonNull(charset, "charset is null");
    return this;
  }
}
