package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;
import com.itextpdf.text.pdf.PdfCopy;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class SplitBySizePdfHandler extends AbstractPdfHandler {

  private final long maxSize;
  
  public SplitBySizePdfHandler(long maxSize) {
    this.maxSize = Args.requirePositive(maxSize, "maxSize is < 1");
  }
  
  @Override
  public long combinedIncrement(long currentCombined, PdfCopy copy) {
    return copy.getCurrentDocumentSize();
  }
  
  @Override
  protected boolean mustSplit(long currentCombinedValue, IPdfPageRange range) {
    return currentCombinedValue > maxSize;
  }
}
