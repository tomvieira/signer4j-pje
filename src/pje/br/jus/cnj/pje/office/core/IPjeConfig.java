package br.jus.cnj.pje.office.core;

import java.io.File;
import java.io.IOException;

public interface IPjeConfig {
  File getConfigFile() throws IOException;
  
  void reset();
}
