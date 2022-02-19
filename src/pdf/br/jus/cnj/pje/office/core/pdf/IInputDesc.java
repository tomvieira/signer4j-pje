package br.jus.cnj.pje.office.core.pdf;

import java.io.File;

public interface IInputDesc {
  Iterable<File> getInputPdfs();
  File resolveOutput(String fileName);
}
