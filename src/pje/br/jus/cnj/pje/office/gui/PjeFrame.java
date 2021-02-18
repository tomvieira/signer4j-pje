package br.jus.cnj.pje.office.gui;

import java.awt.GraphicsConfiguration;

import com.github.signer4j.gui.utils.JEscFrame;

public class PjeFrame extends JEscFrame {
  private static final long serialVersionUID = 1L;

  public PjeFrame(String title) {
    super(title);
    setupIcon();
  }

  public PjeFrame(String title, GraphicsConfiguration gc) {
    super(title, gc);
    setupIcon();
  }

  private void setupIcon() {
    this.setIconImage(Images.PJE_ICON.asImage());
  }
  
  public void showToFront(){
    this.setVisible(true); 
    this.toFront();
  }
  
  public void close() {
    this.setVisible(false);
    this.dispose();
  }
}
