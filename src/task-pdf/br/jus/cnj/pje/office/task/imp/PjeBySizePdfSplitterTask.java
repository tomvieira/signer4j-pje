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

import static com.github.utils4j.gui.imp.Dialogs.getInteger;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.FileWrapper;
import com.github.filehandler4j.imp.InputDescriptor;
import com.github.pdfhandler4j.imp.BySizePdfSplitter;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.pdfhandler4j.imp.event.PdfEndEvent;
import com.github.pdfhandler4j.imp.event.PdfPageEvent;
import com.github.pdfhandler4j.imp.event.PdfReadingEnd;
import com.github.pdfhandler4j.imp.event.PdfReadingStart;
import com.github.pdfhandler4j.imp.event.PdfStartEvent;
import com.github.progress4j.IQuietlyProgress;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoTamanho;

class PjeBySizePdfSplitterTask extends PjeMediaProcessingTask<ITarefaPdfDivisaoTamanho> {
  
  private long tamanho;
  
  protected PjeBySizePdfSplitterTask(Params request, ITarefaPdfDivisaoTamanho pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateTaskParams() throws TaskException, InterruptedException {
    super.validateTaskParams();
    this.tamanho = getPojoParams().getTamanho();
    if (this.tamanho == 0) {
      Optional<Integer> total = ofNullable(getInteger(
        "Tamanho máximo do arquivo (MB):", 
        10, 
        2, 
        1024
      ));
      this.tamanho = total.orElseThrow(InterruptedException::new);
    }
  }

  @Override
  protected boolean process(Path file, IQuietlyProgress progress) {
    Path parent = file.getParent();
    IInputFile input = new FileWrapper(file.toFile());
    Path output = parent.resolve(input.getShortName() + "_(VOLUMES DE ATÉ " + tamanho + "MB)");
    InputDescriptor desc;
    try {
      desc = new PdfInputDescriptor.Builder()
        .add(input)
        .output(output)
        .build();
    } catch (IOException e1) {
      progress.abort(e1);
      LOGGER.error("Não foi possível criar pasta " + output.toString(), e1);
      return false;
    }

    AtomicBoolean success = new AtomicBoolean(true);
    
    new BySizePdfSplitter(tamanho * 1000 * 1000).apply(desc).subscribe(
      e -> {
        if (e instanceof PdfReadingStart) {
          progress.begin(SplitterStage.READING);
        } else if (e instanceof PdfStartEvent) {
          progress.begin(SplitterStage.SPLITING, ((PdfStartEvent)e).getTotalPages());            
        } else if (e instanceof PdfReadingEnd || e instanceof PdfEndEvent) {
          progress.end();
        } else if (e instanceof PdfPageEvent) {
          progress.step(e.getMessage());
        } else {
          progress.info(e.getMessage());  
        }
      },
      e -> {
        success.set(false);
        output.toFile().delete();
      }
    ); 
  
    return success.get();
  }
}
