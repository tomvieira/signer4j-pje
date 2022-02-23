package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;
import com.itextpdf.text.pdf.PdfCopy;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class SplitBySizePdfHandler extends SplitByVolumePdfHandler {

  private final long maxSize;
  
  public SplitBySizePdfHandler(long maxSize) {
    this.maxSize = Args.requirePositive(maxSize, "maxSize is < 1");
  }
  
  @Override
  public long combinedIncrement(long currentCombined, PdfCopy copy) {
    long size = copy.getCurrentDocumentSize(); 
    return size += 0.03 * size;
  }
  
  @Override
  protected boolean mustSplit(long currentCombinedValue, IPdfPageRange range, long max, int totalPages) {
    return currentCombinedValue + max + 2 * (maxSize / totalPages) > maxSize;
  }
}
