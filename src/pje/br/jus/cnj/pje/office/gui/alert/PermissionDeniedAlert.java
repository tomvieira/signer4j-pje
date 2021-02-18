package br.jus.cnj.pje.office.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import br.jus.cnj.pje.office.gui.Images;

public final class PermissionDeniedAlert {

  public static boolean display(String message) {
    return new PermissionDeniedAlert(message).show();
  }
  
  private static final String[] OPTIONS = {"ENTENDI"};
  
  private final JOptionPane jop;
  
  private PermissionDeniedAlert(String message) {
    jop = new JOptionPane(
      message,
      JOptionPane.INFORMATION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      Images.PJE_LOCK.asIcon(), 
      OPTIONS, 
      OPTIONS[0]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Permissão Negada!");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Images.PJE_ICON.asImage());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
