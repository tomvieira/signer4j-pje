package br.jus.cnj.pje.office.core.imp;

import java.io.File;
import java.io.IOException;

import br.jus.cnj.pje.office.core.IPjeConfig;

public enum PjeConfig implements IPjeConfig {
  INSTANCE(new File(System.getProperty("user.home"), ".pjeoffice-pro"));

  private final File config;

  PjeConfig(File baseFolder) {
    baseFolder.mkdirs();
    this.config = new File(baseFolder, "pjeoffice-pro.config");
  }
  
  @Override
  public void reset() {
    this.config.delete();
  }
  
  @Override
  public File getConfigFile() throws IOException {
    if (!config.exists() && !config.createNewFile()) {
      throw new IOException("Não foi possível criar o arquivo de configuração em: " + config.getAbsolutePath());
    }
    return config;
  }
}
