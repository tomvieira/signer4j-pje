package br.jus.cnj.pje.office.core.pdf.imp;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class SplitByPagesPdfHandler extends SplitByVolumePdfHandler{

  public SplitByPagesPdfHandler(IPdfPageRange... ranges) {
    super(ranges);
  }

  @Override
  public long combinedStart(IPdfPageRange range) {
    return range.startPage();
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPdfPageRange range, long max, int totalPages) {
    return currentCombined >= range.endPage();
  };
  
  @Override
  protected boolean breakOnSplit() {
    return true;
  }
}
