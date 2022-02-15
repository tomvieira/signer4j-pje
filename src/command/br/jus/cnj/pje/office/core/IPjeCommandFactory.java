package br.jus.cnj.pje.office.core;

import com.github.signer4j.IBootable;

public interface IPjeCommandFactory {
  
  public IPjeCommander<?, ?> create(IBootable bootable);
}
