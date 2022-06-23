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

import static com.github.utils4j.gui.imp.SwingTools.invokeAndWaitT;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.github.filehandler4j.imp.InputDescriptor;
import com.github.pdfhandler4j.imp.JoinPdfHandler;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.pdfhandler4j.imp.PdfInputDescriptor.Builder;
import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.progress4j.imp.QuietlyProgress;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.gui.imp.FileListWindow;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.imp.PjeConfig;
import br.jus.cnj.pje.office.task.ITarefaMedia;

class PjeJoinPdfTask extends PjeAbstractMediaTask<ITarefaMedia> {
  
  private static enum Stage implements IStage {
    MERGING("Unindo arquivos");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  protected PjeJoinPdfTask(Params request, ITarefaMedia pojo) {
    super(request, pojo);
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {

    IProgress progress = getProgress();
    
    progress.info("Aguardando ordenação dos arquivos");
    
    AtomicReference<Path> parent = new AtomicReference<>();

    List<File> files = arquivos.stream()
      .filter(Strings::hasText)
      .map(s -> new File(s))
      .filter(File::exists)
      .peek(f -> parent.set(f.toPath().getParent()))
      .sorted((a, b) -> a.getName().compareTo(b.getName()))
      .collect(toList());
    
    int size = files.size();
    
    if (size == 1) {
      throw showFail("A união de PDF's exige que sejam selecionados 2 ou mais arquivos.");
    }

    Path output = parent.get();
    Optional<String> fileName;
    
    do {
      fileName = invokeAndWaitT(new FileListWindow(PjeConfig.getIcon(), files)::getFileName);
      
      if (!fileName.isPresent()) {
        throw new InterruptedException();
      }
      
      String fname = fileName.get() + ".pdf";
      
      if (!output.resolve(fname).toFile().exists())
        break;
      
      Dialogs.Choice override = Dialogs.getBoolean("Arquivo '" + fname + "' já existe! Deseja sobrescrever?", "Atenção!", false); 
      if (override == Dialogs.Choice.YES)
        break;
      
    } while(true);
    
    progress.info("Arquivos ordenados");

    Builder builder = new PdfInputDescriptor.Builder();
    
    files.forEach(builder::add);
    
    progress.begin(Stage.MERGING, 4 * size + 1);

    InputDescriptor desc;
    try {
      desc = builder.output(output).build();
    } catch (IOException e) {
      throw progress.abort(showFail(
        "Não foi possível gerar arquivo de saída.", 
        output.toString() + " é pasta válida com permissão de escrita?", e)
      );
    }
    
    AtomicBoolean success = new AtomicBoolean(true);
    
    IProgress quietly = QuietlyProgress.wrap(progress);
    new JoinPdfHandler(fileName.get())
      .apply(desc)
      .subscribe(
        (e) -> quietly.step(e.getMessage()),
        (e) -> {
          quietly.abort(e);
          success.set(false);
        }
      );
    
    if (!success.get()) {
      throw showFail("Não foi possível unir os arquivos.", progress.getAbortCause());
    }

    progress.info("Unidos " + size + " arquivos"); 
    
    progress.end();
   
    showInfo("Arquivos unidos com sucesso.", "Ótimo!");
    return success();    
  }
}
