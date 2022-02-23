package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public final class PageRange implements IPdfPageRange {

  private final int startPage;
  private final int endPage;
  
  public PageRange() {
    this(1, Integer.MAX_VALUE);
  }

  public PageRange(int startPage, int endPage) {
    Args.requireTrue(startPage <= endPage, "startPage > endPage");
    this.startPage = Args.requirePositive(startPage, "stargPage isn't positive");
    this.endPage = endPage;
  }
  
  @Override
  public final int startPage() {
    return startPage;
  }

  @Override
  public final int endPage() {
    return endPage;
  }
}
