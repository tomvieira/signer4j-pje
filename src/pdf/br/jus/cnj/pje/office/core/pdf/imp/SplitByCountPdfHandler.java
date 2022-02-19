package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class SplitByCountPdfHandler extends AbstractPdfHandler {

  private final int pageCount;
  
  public SplitByCountPdfHandler(int pageCount) {
    this.pageCount = Args.requirePositive(pageCount, "pageCount is < 1");
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPdfPageRange range) {
    return currentCombined >= pageCount;
  }
}
