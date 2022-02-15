package br.jus.cnj.pje.office.task.imp;

import java.util.Optional;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.task.IMainParams;

public class MainOrigin extends MainWrapper {

  private final Optional<String> origin;
  
  public MainOrigin(IMainParams main, Optional<String> origin) {
    super(main);
    this.origin = Args.requireNonNull(origin, "origin is null");    
  }
  
  @Override
  public final Optional<String> getOrigin() {
    return origin;
  }
}
