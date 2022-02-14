package br.jus.cnj.pje.office.core.pdf;

import java.io.File;
import java.util.Optional;

public class PdfStatus implements IPdfStatus {

  private final String message;
  private final Optional<File> output;

  public PdfStatus(String message) {
    this(message, null);
  }

  public PdfStatus(String message, File output) {
    this.message = message;
    this.output = Optional.ofNullable(output);
  }
  
  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Optional<File> getOutput() {
    return output;
  }
}
