package br.jus.cnj.pje.office;

import java.awt.PopupMenu;


public interface IPjeFrontEnd {
  String getTitle();
  
  IPjeFrontEnd next();
  
  void install(IBootable office, PopupMenu menu) throws Exception;
  
  void dispose();
}
