package br.jus.cnj.pje.office.core.imp;

import com.github.progress4j.imp.ProgressFactory;

public class PjeProgressFactory extends ProgressFactory{

  public static final PjeProgressFactory DEFAULT = new PjeProgressFactory();

  private PjeProgressFactory() {
    super(PjeConfig.getIcon());
  }
}
