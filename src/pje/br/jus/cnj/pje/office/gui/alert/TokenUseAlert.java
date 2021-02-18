package br.jus.cnj.pje.office.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import br.jus.cnj.pje.office.gui.Images;

public final class TokenUseAlert {

  private static final String MESSAGE_FORMAT = "Há uma solicitação de uso do seu "
      + "certificado digital!";
  
  private static final String[] OPTIONS = {"Ok! Fui eu mesmo que solicitei", "Não reconheço esta tentativa"};
  
  public static boolean display() {
    return new TokenUseAlert().show();
  }
  
  private final JOptionPane jop;
  
  private TokenUseAlert() {
    jop = new JOptionPane(
      MESSAGE_FORMAT,
      JOptionPane.QUESTION_MESSAGE, 
      JOptionPane.OK_OPTION, 
      Images.PJE_CERTIFICATE.asIcon(), 
      OPTIONS, 
      OPTIONS[1]
    );
  }

  private boolean show() {
    JDialog dialog = jop.createDialog("Uso do certificado");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Images.PJE_ICON.asImage());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
