package br.jus.cnj.pje.office.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;

import javax.swing.ImageIcon;

import com.github.signer4j.gui.utils.IPicture;

public enum PjeImages implements IPicture {
  PJE_ICON("/pje-icon.png"), 
  
  PJE_SERVER("/server.png"),
  
  PJE_ICON_TRAY("/pje-icon-16.png");
  
  final String path;
  
  PjeImages(String path) {
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


