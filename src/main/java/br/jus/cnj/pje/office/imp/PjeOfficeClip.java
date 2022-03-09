
package br.jus.cnj.pje.office.imp;  

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.core.imp.PjeLifeCycleFactory;

public class PjeOfficeClip extends PjeOfficeApp {

  public static void main(String[] args) {
    invokeLater(() ->  new PjeOfficeClip().start());
  }

  private PjeOfficeClip() {
    super(PjeLifeCycleFactory.CLIP);
    super.office.setDevMode();
  }
}
