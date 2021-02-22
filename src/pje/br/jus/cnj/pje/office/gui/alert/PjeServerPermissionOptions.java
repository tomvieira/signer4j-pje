package br.jus.cnj.pje.office.gui.alert;

import static java.lang.String.format;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Config;

import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.imp.PjeAccessTime;
import br.jus.cnj.pje.office.gui.PjeImages;

public final class PjeServerPermissionOptions {

  private static final String MESSAGE_FORMAT = "A aplicação '%s' deseja acessar o PJeOffice.\n\nServidor: %s\n\nDeseja autorizar?\n\n";

  private static final PjeAccessTime[] OPTIONS = PjeAccessTime.values();
  
  public static PjeAccessTime choose(IPjeServerAccess access) {
    Args.requireNonNull(access, "access is null");
    return new PjeServerPermissionOptions(access).show();
  }
  
  private final JOptionPane jop;
  
  private PjeServerPermissionOptions(IPjeServerAccess access) {
    jop = new JOptionPane(
      format(MESSAGE_FORMAT, access.getApp(), access.getServer()),
      JOptionPane.QUESTION_MESSAGE, 
      JOptionPane.YES_NO_CANCEL_OPTION, 
      PjeImages.PJE_SERVER.asIcon(), 
      OPTIONS, 
      OPTIONS[2]
    );
  }

  private PjeAccessTime show() {
    JDialog dialog = jop.createDialog("Autorização de servidor");
    dialog.setAlwaysOnTop(true);
    dialog.setModal(true);
    dialog.setIconImage(Config.getIcon());
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = jop.getValue();
    int i = 0, length = OPTIONS.length;
    for (; i < length; i++) {
      PjeAccessTime at = OPTIONS[i];
      if (at.equals(selectedValue)) {
        return at;
      }
    }
    return PjeAccessTime.NOT;
  }
}
