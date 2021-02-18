package br.jus.cnj.pje.office.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import br.jus.cnj.pje.office.gui.Images;

public final class ExpiredPasswordAlert {

  private static final String MESSAGE_FORMAT = "A senha do seu dispositivo expirou e deve ser renovada!\n\n"
      + "O PjeOffice não renova a sua senha! A renovação deve ser feita utilizando o software\n"
      + "fornecido pelo fabricante do seu dispostivo.";
  
  private static final String[] OPTIONS = {"ENTENDI"};
  
  public static boolean display() {
    return new ExpiredPasswordAlert().show();
  }
  
  private final JOptionPane jop;
  
  private ExpiredPasswordAlert() {
    jop = new JOptionPane(
      MESSAGE_FORMAT,
      JOptionPane.INFORMATION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      Images.PJE_LOCK.asIcon(), 
      OPTIONS, 
      OPTIONS[0]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Senha expirada");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Images.PJE_ICON.asImage());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
