package br.jus.cnj.pje.office.core.imp;

import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.signer4j.progress.imp.ProgressFactory;

public class PjeProgressFactory extends ProgressFactory{

  public static final PjeProgressFactory DEFAULT = new PjeProgressFactory();

  private PjeProgressFactory() {
    super(Config.getIcon(), Images.LOG.asIcon());
  }
}
