package br.jus.cnj.pje.office;

import java.awt.PopupMenu;

import br.jus.cnj.pje.office.core.imp.IPjeOffice;

public interface IPjeFrontEnd {
  
  void install(IPjeOffice office, PopupMenu menu) throws Exception;
  
  void dispose();
}
