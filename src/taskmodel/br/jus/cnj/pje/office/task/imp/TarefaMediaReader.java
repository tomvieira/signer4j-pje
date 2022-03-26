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

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IJsonTranslator;
import br.jus.cnj.pje.office.task.ITarefaMedia;

/*************************************************************************************
 * Classe base para todos os manipuladores de medias (PDF's, VÍDEOS, etc)
/*************************************************************************************/

abstract class TarefaMediaReader<T> extends AbstractRequestReader<Params, T> implements IJsonTranslator {

  static class TarefaMedia implements ITarefaMedia {
    protected List<String> arquivos = new ArrayList<>();
    
    @Override
    public final List<String> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }

  protected TarefaMediaReader(Class<?> clazz) {
    super(clazz);
  }
  
  @Override
  public String toJson(Params param) throws Exception {
    return MAIN.toJson(param
      .of("tarefaId", getTarefaId())
      .of("tarefa", getTarefa(param))
    );
  }

  protected abstract String getTarefaId();
  protected abstract Object getTarefa(Params input);
}


