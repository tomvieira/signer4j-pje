
package br.jus.cnj.pje.office.imp;  

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.core.imp.PjeLifeCycleFactory;

public class PjeOfficeFileWatch extends PjeOfficeApp {
  
  public static void main(String[] args) {
    invokeLater(() ->  new PjeOfficeFileWatch().start());
  }

  private PjeOfficeFileWatch() {
    super(PjeLifeCycleFactory.FILEWATCH);
    super.office.setDevMode();
  }
}
