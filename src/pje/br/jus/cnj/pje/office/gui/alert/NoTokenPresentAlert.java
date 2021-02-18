package br.jus.cnj.pje.office.gui.alert;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import br.jus.cnj.pje.office.gui.Images;

public final class NoTokenPresentAlert {

  private static final String MESSAGE_FORMAT = "Não foi encontrado um certificado disponível.\n\n"
      + "1) Se seu certificado é do tipo A3, tenha certeza de que\n"
      + "  esteja conectado no seu computador e funcional.\n"
      + "2) Se seu certificado é do tipo A1, tenha certeza que tenha\n"
      + "  sido instalado no computador e configurado no PjeOffice.\n\n"
      + "Tentar identificar novamente?";
  
  private static final String[] OPTIONS = {"SIM", "NÃO"};
  
  public static boolean display() {
    return new NoTokenPresentAlert().show();
  }
  
  private final JOptionPane jop;
  
  private NoTokenPresentAlert() {
    jop = new JOptionPane(
      MESSAGE_FORMAT,
      JOptionPane.QUESTION_MESSAGE, 
      JOptionPane.YES_NO_OPTION, 
      Images.PJE_CERTIFICATE.asIcon(), 
      OPTIONS, 
      OPTIONS[0]
    );
  }
  
  private boolean show() {
    JDialog dialog = jop.createDialog("Certificado não encontrado");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Images.PJE_ICON.asImage());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    return OPTIONS[0].equals(selectedValue);
  }
}
