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

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.VIDEO_SPLIT_BY_SIZE;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoTamanho;


/*************************************************************************************
 * Leitor para divisão de VÍDEOS por tamanho específico
/*************************************************************************************/

class TarefaVideoDivisaoTamanhoReader extends TarefaMediaReader<ITarefaVideoDivisaoTamanho> {

  public static final TarefaVideoDivisaoTamanhoReader INSTANCE = new TarefaVideoDivisaoTamanhoReader();
  
  final static class TarefaVideoDivisaoTamanho extends TarefaMedia implements ITarefaVideoDivisaoTamanho {
    private long tamanho;
    
    public final long getTamanho(){
      return tamanho;
    }
  }
  
  private TarefaVideoDivisaoTamanhoReader() {
    super(TarefaVideoDivisaoTamanho.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaVideoDivisaoTamanho pojo) throws IOException {
    return new PjeBySizeVideoSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return VIDEO_SPLIT_BY_SIZE.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    TarefaVideoDivisaoTamanho tarefaTamanho = new TarefaVideoDivisaoTamanho();
    tarefaTamanho.tamanho = Long.parseLong(param.getValue("tamanho"));
    tarefaTamanho.arquivos = param.getValue("arquivos");
    return tarefaTamanho;
  }
}
