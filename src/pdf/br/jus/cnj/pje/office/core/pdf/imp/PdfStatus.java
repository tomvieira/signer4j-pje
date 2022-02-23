package br.jus.cnj.pje.office.core.pdf.imp;

import java.io.File;
import java.util.Optional;

import br.jus.cnj.pje.office.core.pdf.IPdfStatus;

public class PdfStatus implements IPdfStatus {

  private final String message;
  private final Optional<File> output;
  private final int currentPage;

  public PdfStatus(String message, int currentPage) {
    this(message, currentPage, null);
  }

  public PdfStatus(String message, int currentPage, File output) {
    this.message = message;
    this.currentPage = currentPage;
    this.output = Optional.ofNullable(output);
  }
  
  @Override
  public final String getMessage() {
    return message;
  }
  
  @Override
  public final int geCurrentPage() {
    return currentPage;
  }

  @Override
  public final Optional<File> getOutput() {
    return output;
  }
  
  @Override
  public final String toString() {
    return message + " pg: " + currentPage;
  }
}
