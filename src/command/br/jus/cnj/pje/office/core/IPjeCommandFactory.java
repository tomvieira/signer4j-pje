package br.jus.cnj.pje.office.core;

import com.github.signer4j.IFinishable;

public interface IPjeCommandFactory {
  
  public IPjeCommander<?, ?> create(IFinishable finishingCode);
}
