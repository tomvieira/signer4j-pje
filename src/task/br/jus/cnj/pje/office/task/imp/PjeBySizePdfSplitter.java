package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.FileWrapper;
import com.github.filehandler4j.imp.InputDescriptor;
import com.github.pdfhandler4j.imp.BySizePdfSplitter;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoTamanho;

class PjeBySizePdfSplitter extends PjeAbstractPdfTask<ITarefaPdfDivisaoTamanho> {
  
  private static enum Stage implements IStage {
    SPLITING ("Dividindo arquivos");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private long tamanho;
  
  protected PjeBySizePdfSplitter(Params request, ITarefaPdfDivisaoTamanho pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    ITarefaPdfDivisaoTamanho pojo = getPojoParams();
    this.tamanho = pojo.getTamanho();
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    final int size = arquivos.size();
    
    progress.begin(Stage.SPLITING);

    for(int i = 0; i < size; i++) {
      Path file = Paths.get(arquivos.get(i));
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
        throw progress.abort(new TaskException("Não foi possível criar pasta " + output.toString()));
      }

      new BySizePdfSplitter(tamanho * 1024 * 1024).apply(desc).subscribe(
        (e) -> progress.info(e.getMessage()),
        (e) -> output.toFile().delete()
      );
      
      progress.info("Dividido arquivo %s", file);
    }
    progress.end();
    return success();
  }
}
