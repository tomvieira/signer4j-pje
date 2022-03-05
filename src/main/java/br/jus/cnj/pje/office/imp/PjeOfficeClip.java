
package br.jus.cnj.pje.office.imp;  

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.core.imp.PjeCommandFactory;

public class PjeOfficeClip extends PjeOfficeApp {

  public static void main(String[] args) {
    invokeLater(() ->  new PjeOfficeClip().start());
  }

  private PjeOfficeClip() {
    super(PjeCommandFactory.CLIP);
    super.office.setDevMode();
  }
}
