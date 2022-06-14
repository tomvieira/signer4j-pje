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

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.progress4j.IProgress;
import com.github.progress4j.IQuietlyProgress;
import com.github.progress4j.IStage;
import com.github.progress4j.imp.QuietlyProgress;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaMedia;

abstract class PjeMediaProcessingTask<T extends ITarefaMedia> extends PjeAbstractMediaTask<T> {
  
  public enum SplitterStage implements IStage {
    PROCESSING("Processando arquivos"),
    READING("Lendo o arquivo (seja paciente...)"),
    SPLITING ("Dividindo arquivos"),
    CONVERTING ("Convertendo arquivos (seja paciente...)"),
    SPLITTING_PATIENT("Dividindo arquivos (seja paciente...)");
    
    private final String message;

    SplitterStage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }

  protected PjeMediaProcessingTask(Params request, T pojo) {
    super(request, pojo);
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    IQuietlyProgress quietly =  QuietlyProgress.wrap(progress);//SingleThreadProgress.wrap(progress));
    final int size = arquivos.size();
    
    boolean success = true;
    
    progress.begin(SplitterStage.PROCESSING, size);
    for(int i = 0; i < size; i++) {
      Path file = Paths.get(arquivos.get(i));

      success &= process(file, quietly);
      
      progress.step("Gerado arquivo %s", file);
    }
    
    if (!success) {
      throw showFail("Alguns arquivos não puderam ser gerados.", progress.getAbortCause());
    }
    
    progress.end();
    
    showInfo("Arquivos gerados com sucesso.", "Ótimo!");
    return success();
  }

  protected abstract boolean process(Path file, IQuietlyProgress progress);
}
