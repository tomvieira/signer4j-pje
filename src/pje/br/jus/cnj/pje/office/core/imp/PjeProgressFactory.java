package br.jus.cnj.pje.office.core.imp;

import com.github.signer4j.progress.imp.Images;
import com.github.signer4j.progress.imp.ProgressFactory;

public class PjeProgressFactory extends ProgressFactory{

  public static final PjeProgressFactory DEFAULT = new PjeProgressFactory();

  private PjeProgressFactory() {
    super(PjeConfig.getIcon(), Images.LOG.asIcon());
  }
}
