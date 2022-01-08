package br.jus.cnj.pje.office.core.imp;

import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IArquivo;

class ArquivoWrapper implements IArquivo {

  private IArquivo arquivo;
  
  protected ArquivoWrapper(IArquivo arquivo) {
    this.arquivo = Args.requireNonNull(arquivo, "arquivo is null");
  }
  
  public Optional<String> getUrl() {
    return arquivo.getUrl();
  }

  public Optional<String> getNome() {
    return arquivo.getNome();
  }

  public boolean isTerAtributosAssinados() {
    return arquivo.isTerAtributosAssinados();
  }

  public List<String> getParamsEnvio() {
    return arquivo.getParamsEnvio();
  }
}