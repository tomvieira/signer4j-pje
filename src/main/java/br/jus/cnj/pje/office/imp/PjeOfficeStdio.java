
package br.jus.cnj.pje.office.imp;  

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.core.imp.PjeLifeCycleFactory;

public class PjeOfficeStdio extends PjeOfficeApp {
  
  public static void main(String[] args) {
    invokeLater(() ->  new PjeOfficeStdio().start());
  }

  private PjeOfficeStdio() {
    super(PjeLifeCycleFactory.STDIO);
    super.office.setDevMode();
  }
}
