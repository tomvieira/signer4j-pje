package br.jus.cnj.pje.office.core.pdf;

import java.io.File;
import java.nio.file.Path;

public interface IInputDesc {
  Iterable<Path> getInputPdfs();
  Path getOutputFolder();
  String getNamePrefix();
  String getNameSuffix();
  File resolveOutput(String fileName);
}
