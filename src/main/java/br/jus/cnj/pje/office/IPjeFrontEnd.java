package br.jus.cnj.pje.office;

import java.awt.PopupMenu;

import br.jus.cnj.pje.office.core.IPjeOffice;

public interface IPjeFrontEnd {
  String getTitle();
  
  IPjeFrontEnd next();
  
  void install(IPjeOffice office, PopupMenu menu) throws Exception;
  
  void dispose();
}
