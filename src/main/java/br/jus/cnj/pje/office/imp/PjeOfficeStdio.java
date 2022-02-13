
package br.jus.cnj.pje.office.imp;  

import static com.github.signer4j.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.core.imp.PjeCommandFactory;

public class PjeOfficeStdio extends PjeOfficeApp {
  
  public static void main(String[] args) {
    invokeLater(() ->  new PjeOfficeStdio().start());
  }

  private PjeOfficeStdio() {
    super(PjeCommandFactory.STDIO);
    super.office.setDevMode();
  }
}
