package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Strings;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public abstract class SplitByVolumePdfHandler extends AbstractPdfHandler {

  private int currentVolume = 1;
  
  protected SplitByVolumePdfHandler() {}
  
  protected SplitByVolumePdfHandler(IPdfPageRange[] ranges) {
    super(ranges);
  }

  @Override
  protected String computeFileName(int beginPage) {
    return "VOL-" + Strings.leftFill(currentVolume++, 2, '0') + " (pg-" + beginPage + ")";
  }
}
