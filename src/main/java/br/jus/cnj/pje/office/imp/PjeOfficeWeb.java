
package br.jus.cnj.pje.office.imp;  

import static br.jus.cnj.pje.office.imp.PjeOfficeFrontEnd.getBest;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.imp.PjeCommandFactory;

public class PjeOfficeWeb extends PjeOfficeClassic {

  private static PjeOfficeClassic createInstance(IPjeFrontEnd front) {
    return new PjeOfficeWeb(front);
  }

  public static void main(String[] args) {
    
    for(int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
    }
    invokeLater(() ->  createInstance(getBest()).start());
  }

  private PjeOfficeWeb(IPjeFrontEnd frontEnd) {
    super(frontEnd, PjeCommandFactory.WEB);
  }

  @Override
  protected PjeOfficeClassic newInstance(IPjeFrontEnd front, String origin) {
    return createInstance(front);
  }
}
