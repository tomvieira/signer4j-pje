package br.jus.cnj.pje.office.task.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;

import br.jus.cnj.pje.office.task.IPjeSignMode;
import br.jus.cnj.pje.office.task.ITarefaAssinador;
import br.jus.cnj.pje.office.web.IPjeResponse;

enum PjeSignMode implements IPjeSignMode {
  LOCAL("LOCAL") {
    @Override
    public ITask<IPjeResponse> getTask(Params params, ITarefaAssinador pojo) {
      return new PjeAssinadorLocalTask(params, pojo);
    }
  }, 
  REMOTO("REMOTO"){
    @Override
    public ITask<IPjeResponse> getTask(Params params, ITarefaAssinador pojo) {
      return new PjeAssinadorRemotoTask(params, pojo);
    }
  };

  private static final PjeSignMode[] VALUES = PjeSignMode.values(); 
  
  @JsonCreator
  public static IPjeSignMode fromString(final String key) {
    return get(key).orElse(null);
  }

  private String name;
  
  PjeSignMode(String name) {
    this.name = name;
  }
  
  @JsonValue
  public String getKey() {
    return name.toLowerCase();
  }

  public static Optional<IPjeSignMode> get(String name) {
    for(PjeSignMode a: VALUES) {
      if (a.name.equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}
