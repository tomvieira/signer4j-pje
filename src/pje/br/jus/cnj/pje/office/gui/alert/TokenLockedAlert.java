package br.jus.cnj.pje.office.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import br.jus.cnj.pje.office.gui.Images;

public final class TokenLockedAlert {

  private static final String MESSAGE_FORMAT = "Infelizmente seu dispositivo encontra-se BLOQUEADO.\n\n"
      + "A única forma de desbloqueá-lo é fazendo uso da sua senha de administração,\n"
      + "também conhecida como PUK.\n\n"
      + "Se você desconhece sua senha PUK, seu certificado foi perdido para sempre\n"
      + "e um novo certificado deverá ser providenciado.";
  
  private static final String[] OPTIONS = {"ENTENDI"};
  
  public static boolean display() {
    return new TokenLockedAlert().show();
  }
  
  private final JOptionPane jop;
  
  private TokenLockedAlert() {
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
    JDialog dialog = jop.createDialog("Dispositivo bloqueado");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Images.PJE_ICON.asImage());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
