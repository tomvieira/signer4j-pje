package br.jus.cnj.pje.office.gui;

import com.github.utils4j.gui.IPicture;

public enum PjeImages implements IPicture {
  PJE_ICON("/pje-icon.png"), 
  
  PJE_SERVER("/server.png"),
  
  PJE_ICON_TRAY("/pje-icon-16.png");
  
  final String path;
  
  PjeImages(String path) {
    this.path = path;
  }

  @Override
  public String path() {
    return path;
  }
}


