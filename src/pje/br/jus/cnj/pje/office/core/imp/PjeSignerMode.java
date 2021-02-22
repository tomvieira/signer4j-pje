package br.jus.cnj.pje.office.core.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;

import br.jus.cnj.pje.office.core.IAssinadorParams;
import br.jus.cnj.pje.office.core.ISignerMode;
import br.jus.cnj.pje.office.web.IPjeResponse;

public enum PjeSignerMode implements ISignerMode {
  LOCAL("LOCAL") {
    @Override
    public ITask<IPjeResponse> getTask(Params params, IAssinadorParams pojo) {
      return new PjeAssinadorLocalTask(params, pojo);
    }
  }, 
  REMOTO("REMOTO"){
    @Override
    public ITask<IPjeResponse> getTask(Params params, IAssinadorParams pojo) {
      return new PjeAssinadorRemotoTask(params, pojo);
    }
  };

  private static final PjeSignerMode[] VALUES = PjeSignerMode.values(); 
  
  @JsonCreator
  public static ISignerMode fromString(final String key) {
    return get(key).orElse(null);
  }

  private String name;
  
  PjeSignerMode(String name) {
    this.name = name;
  }
  
  @JsonValue
  public String getKey() {
    return name.toLowerCase();
  }

  public static Optional<ISignerMode> get(String name) {
    for(PjeSignerMode a: VALUES) {
      if (a.name.equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}
