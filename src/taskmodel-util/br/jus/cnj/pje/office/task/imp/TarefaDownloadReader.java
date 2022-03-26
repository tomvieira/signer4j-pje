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

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.UTIL_DOWNLOADER;

import java.io.IOException;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.ITarefaDownload;

class TarefaDownloadReader extends TarefaMediaReader<ITarefaDownload>{

  public static final TarefaDownloadReader INSTANCE = new TarefaDownloadReader();
  
  final static class TarefaDownload implements ITarefaDownload {
    private String url;
    private String enviarPara;
    
    @Override
    public final Optional<String> getUrl() {
      return Strings.optional(url);
    }

    @Override
    public Optional<String> getEnviarPara() {
      return Strings.optional(enviarPara);
    }
  }
  
  private TarefaDownloadReader() {
    super(TarefaDownload.class);
  }
  
  @Override
  protected ITask<?> createTask(Params output, ITarefaDownload pojo) throws IOException {
    return new PjeDownloadTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return UTIL_DOWNLOADER.getId();
  }

  @Override
  protected Object getTarefa(Params input) {
    TarefaDownload td = new TarefaDownload();
    td.url = input.getValue("url");
    td.enviarPara = input.getValue("enviarPara");
    return td;
  }
}
