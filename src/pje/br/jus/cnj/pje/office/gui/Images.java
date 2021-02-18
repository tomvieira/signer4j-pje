package br.jus.cnj.pje.office.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;

import javax.swing.ImageIcon;

public enum Images {
  PJE_ICON("/pje-icon.png"), 
  
  PJE_SERVER("/server.png"),
  
  PJE_LOCK("/lock.png"),
  
  PJE_CERTIFICATE("/certificate.png"),
  
  PJE_LOG("/log.png"),
  
  PJE_ICON_TRAY("/pje-icon-16.png"), 
  
  PJE_ICON_A3("/a3.png"), 
  
  PJE_ICON_A1("/a1.png");

  final String path;
  
  Images(String path) {
    this.path = path;
  }
  
  public InputStream asStream() {
    return getClass().getResourceAsStream(path);
  }
  
  public Image asImage() {
    return Toolkit.getDefaultToolkit().createImage(getClass().getResource(path));
  }

  public ImageIcon asIcon() {
    return new ImageIcon(getClass().getResource(path));
  }
}


