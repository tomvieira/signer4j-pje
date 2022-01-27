package br.jus.cnj.pje.office;

import java.awt.PopupMenu;

import com.github.signer4j.IFinishable;

public interface IPjeFrontEnd {
  String getTitle();
  
  IPjeFrontEnd next();
  
  void install(IFinishable office, PopupMenu menu) throws Exception;
  
  void dispose();
}
