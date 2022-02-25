package br.jus.cnj.pje.office.task.imp;

import java.util.List;
import java.util.Optional;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.task.IArquivo;

class ArquivoWrapper implements IArquivo {

  private final IArquivo arquivo;
  
  protected ArquivoWrapper(IArquivo arquivo) {
    this.arquivo = Args.requireNonNull(arquivo, "arquivo is null");
  }
  
  @Override
  public final Optional<String> getUrl() {
    return arquivo.getUrl();
  }

  @Override
  public final Optional<String> getNome() {
    return arquivo.getNome();
  }

  @Override
  public final boolean isTerAtributosAssinados() {
    return arquivo.isTerAtributosAssinados();
  }

  @Override
  public final List<String> getParamsEnvio() {
    return arquivo.getParamsEnvio();
  }
}