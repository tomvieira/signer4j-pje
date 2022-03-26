/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.gui.alert;

import static java.lang.String.format;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.github.signer4j.imp.Config;
import com.github.utils4j.imp.Args;

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
      PjeImages.PJE_SERVER.asIcon().orElse(null), 
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
