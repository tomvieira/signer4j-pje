package br.jus.cnj.pje.office.gui;

import java.awt.Frame;

import com.github.signer4j.gui.utils.JEscDialog;

public class PjeDialog extends JEscDialog {
  
  private static final long serialVersionUID = 1L;

  public PjeDialog(String title) {
    this((Frame)null, title);
  }
  
  public PjeDialog(String title, boolean modal) {
    this((Frame)null, title, modal);
  }
  
  public PjeDialog(Frame owner, String title) {
    this(owner, title, false);
  }

  public PjeDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    setupIcon();
  }

  private void setupIcon() {
    this.setIconImage(Images.PJE_ICON.asImage());
  }
  
  public void close() {
    this.setVisible(false);
    this.dispose();
  }
}
