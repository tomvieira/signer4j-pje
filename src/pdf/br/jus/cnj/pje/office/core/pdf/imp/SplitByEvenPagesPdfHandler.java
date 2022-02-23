package br.jus.cnj.pje.office.core.pdf.imp;

public class SplitByEvenPagesPdfHandler extends SplitByParityPdfHandler {
  public SplitByEvenPagesPdfHandler() {
    super(2);
  }
  
  @Override
  protected String computeFileName(int beginPage) {
    return "(páginas pares)";
  }
  
  @Override
  protected int getEndReference(int totalPages) {
    return isOdd(totalPages) ? --totalPages : totalPages;
  }
}
