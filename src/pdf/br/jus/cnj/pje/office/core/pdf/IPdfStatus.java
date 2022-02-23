package br.jus.cnj.pje.office.core.pdf;

import java.io.File;
import java.util.Optional;

public interface IPdfStatus {
  String getMessage();
  int geCurrentPage();
  Optional<File> getOutput();
}
