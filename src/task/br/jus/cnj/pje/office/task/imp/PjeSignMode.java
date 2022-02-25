package br.jus.cnj.pje.office.task.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.IPjeSignMode;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

enum PjeSignMode implements IPjeSignMode {
  LOCAL("LOCAL") {
    @Override
    public ITask<IPjeResponse> getTask(Params params, ITarefaAssinador pojo) {
      return new PjeAssinadorLocalTask(params, pojo);
    }
  }, 
  DEFINIDO("DEFINIDO") {
    @Override
    public ITask<IPjeResponse> getTask(Params params, ITarefaAssinador pojo) {
      return new PjeAssinadorLocalDefinido(params, pojo);
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
