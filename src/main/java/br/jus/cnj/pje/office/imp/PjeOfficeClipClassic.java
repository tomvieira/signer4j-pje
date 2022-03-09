
package br.jus.cnj.pje.office.imp;  

import static br.jus.cnj.pje.office.imp.PjeOfficeFrontEnd.getBest;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.imp.PjeLifeCycleFactory;

public class PjeOfficeClipClassic extends PjeOfficeClassic {

  private static PjeOfficeClassic createInstance(IPjeFrontEnd front, String... args) {
    return new PjeOfficeClipClassic(front, args);
  }

  public static void main(String[] args) {
    invokeLater(() ->  createInstance(getBest(), new String[]{"clipboard"}).start());
  }

  private PjeOfficeClipClassic(IPjeFrontEnd frontEnd, String... args) {
    super(frontEnd, PjeLifeCycleFactory.CLIP, args);
  }

  @Override
  protected PjeOfficeClassic newInstance(IPjeFrontEnd front, String origin) {
    return createInstance(front, origin);
  }
}
