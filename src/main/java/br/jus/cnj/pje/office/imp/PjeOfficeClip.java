
package br.jus.cnj.pje.office.imp;  

import static br.jus.cnj.pje.office.imp.PjeOfficeFrontEnd.getBest;
import static com.github.cutplayer4j.gui.imp.SwingTools.invokeLater;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.imp.PjeCommandFactory;

public class PjeOfficeClip extends PjeOfficeClassic {

  private static PjeOfficeClassic createInstance(IPjeFrontEnd front, String... args) {
    return new PjeOfficeClip(front, args);
  }

  public static void main(String[] args) {
    invokeLater(() ->  createInstance(getBest(), new String[]{"clipboard"}).start());
  }

  private PjeOfficeClip(IPjeFrontEnd frontEnd, String... args) {
    super(frontEnd, PjeCommandFactory.CLIP, args);
  }

  @Override
  protected PjeOfficeClassic newInstance(IPjeFrontEnd front, String origin) {
    return createInstance(front, origin);
  }
}
