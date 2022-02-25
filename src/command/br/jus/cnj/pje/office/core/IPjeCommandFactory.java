package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.IBootable;

public interface IPjeCommandFactory {
  
  public IPjeCommander<?, ?> create(IBootable bootable);
}
