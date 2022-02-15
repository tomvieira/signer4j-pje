package br.jus.cnj.pje.office;

import java.awt.PopupMenu;

import com.github.signer4j.IBootable;

public interface IPjeFrontEnd {
  String getTitle();
  
  IPjeFrontEnd next();
  
  void install(IBootable office, PopupMenu menu) throws Exception;
  
  void dispose();
}
