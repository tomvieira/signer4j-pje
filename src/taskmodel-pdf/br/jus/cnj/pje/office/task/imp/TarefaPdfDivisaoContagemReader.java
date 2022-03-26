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

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_COUNT;

import java.io.IOException;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoContagem;

/*************************************************************************************
 * Leitor para divisão de pdf por contagem de páginas
/*************************************************************************************/

class TarefaPdfDivisaoContagemReader extends TarefaMediaReader<ITarefaPdfDivisaoContagem> {

  public static final TarefaPdfDivisaoContagemReader INSTANCE = new TarefaPdfDivisaoContagemReader();
  
  final static class TarefaPdfDivisaoContagem extends TarefaMedia implements ITarefaPdfDivisaoContagem {
    private long totalPaginas;

    @Override
    public long getTotalPaginas() {
      return totalPaginas;
    }
  }
  
  private TarefaPdfDivisaoContagemReader() {
    super(TarefaPdfDivisaoContagem.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoContagem pojo) throws IOException {
    return new PjeByCountPdfSplitterTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_COUNT.getId();
  }
  
  @Override
  protected Object getTarefa(Params param) {
    TarefaPdfDivisaoContagem tarefaTamanho = new TarefaPdfDivisaoContagem();
    tarefaTamanho.totalPaginas = Long.parseLong(param.getValue("totalPaginas"));
    tarefaTamanho.arquivos = param.getValue("arquivos");
    return tarefaTamanho;
  }
}
