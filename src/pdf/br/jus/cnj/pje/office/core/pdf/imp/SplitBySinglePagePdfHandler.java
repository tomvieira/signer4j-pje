package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Strings;

public class SplitBySinglePagePdfHandler extends SplitByCountPdfHandler {
  public SplitBySinglePagePdfHandler() {
    super(1);
  }
  
  @Override
  protected String computeFileName(int beginPage) {
    return "pg-" + Strings.leftFill(beginPage, 5, '0');
  }
}
