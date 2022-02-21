package br.jus.cnj.pje.office.core.pdf;

import java.io.File;

public interface IInputDescriptor {
  Iterable<File> getInputPdfs();
  File resolveOutput(String fileName);
}
