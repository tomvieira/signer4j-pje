package br.jus.cnj.pje.office.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import br.jus.cnj.pje.office.gui.Images;

public final class MessageAlert {

  private static final String[] OPTIONS = {"ENTENDI"};
  
  public static boolean display(String message) {
    return new MessageAlert(message, OPTIONS).show();
  }
  
  public static boolean display(String message, String textButton) {
    return new MessageAlert(message, new String[] { textButton }).show();
  }
  
  private final JOptionPane jop;
  
  private final String[] options;
  
  private MessageAlert(String message) {
    this(message, OPTIONS);
  }

  private MessageAlert(String message, String[] options) {
    jop = new JOptionPane(
      message,
      JOptionPane.INFORMATION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      null, 
      this.options = options, 
      options[0]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Atenção!");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Images.PJE_ICON.asImage());
    dialog.setVisible(true);
    dialog.toFront();
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return options[0].equals(selectedValue);
  }
}
