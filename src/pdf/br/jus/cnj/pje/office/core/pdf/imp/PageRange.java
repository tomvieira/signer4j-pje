package br.jus.cnj.pje.office.core.pdf.imp;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class PageRange implements IPdfPageRange {

  private final int startPage;
  private final int endPage;
  
  public PageRange() {
    this(1, Integer.MAX_VALUE);
  }

  public PageRange(int startPage, int endPage) {
    this.startPage = startPage;
    this.endPage = endPage;
  }
  
  @Override
  public int startPage() {
    return startPage;
  }

  @Override
  public int endPage() {
    return endPage;
  }
}
