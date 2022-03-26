/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.task.imp;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.IPjeSignMode;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

public enum PjeSignMode implements IPjeSignMode {
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
  public static PjeSignMode fromString(final String key) {
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

  public static Optional<PjeSignMode> get(String name) {
    for(PjeSignMode a: VALUES) {
      if (a.name.equalsIgnoreCase(name))
        return Optional.of(a);
    }
    return Optional.empty();
  }
}
