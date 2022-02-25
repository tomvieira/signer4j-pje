package br.jus.cnj.pje.office.task.imp;

import java.util.Optional;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.task.IMainParams;

public abstract class MainWrapper implements IMainParams {

  private final IMainParams main;
  
  protected MainWrapper(IMainParams main) {
    this.main = Args.requireNonNull(main, "main is null");
  }
  
  @Override
  public Optional<String> getServidor() {
    return main.getServidor();
  }

  @Override
  public Optional<String> getAplicacao() {
    return main.getAplicacao();
  }

  @Override
  public Optional<String> getSessao() {
    return main.getSessao();
  }

  @Override
  public Optional<String> getCodigoSeguranca() {
    return main.getCodigoSeguranca();
  }

  @Override
  public Optional<String> getTarefaId() {
    return main.getTarefaId();
  }

  @Override
  public Optional<String> getTarefa() {
    return main.getTarefa();
  }
  
  @Override
  public Optional<String> getOrigin() {
    return main.getOrigin();
  }

}
