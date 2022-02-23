package br.jus.cnj.pje.office.core.pdf.imp;

public class SplitByOddPagesPdfHandler extends SplitByParityPdfHandler {
  public SplitByOddPagesPdfHandler() {
    super(1);
  }
  
  @Override
  protected String computeFileName(int beginPage) {
    return "(páginas ímpares)";
  }
  
  @Override
  protected int getEndReference(int totalPages) {
    return isEven(totalPages) ? --totalPages : totalPages;
  }
}
