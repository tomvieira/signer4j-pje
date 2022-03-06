package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.FileWrapper;
import com.github.filehandler4j.imp.InputDescriptor;
import com.github.pdfhandler4j.imp.ByEvenPagesPdfSplitter;
import com.github.pdfhandler4j.imp.ByOddPagesPdfSplitter;
import com.github.pdfhandler4j.imp.ByParityPdfSplitter;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoParidade;

class PjeByParityPdfSplitter extends PjeAbstractPdfTask<ITarefaPdfDivisaoParidade> {
  
  private static enum Stage implements IStage {
    SPLITING_EVEN("Excluindo páginas ímpares"),
    SPLITING_ODD("Excluindo páginas pares");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
    
    static Stage of(boolean parity) {
      return parity ? SPLITING_EVEN : SPLITING_ODD;
    }
  }
  
  private boolean paridade;
  
  protected PjeByParityPdfSplitter(Params request, ITarefaPdfDivisaoParidade pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    ITarefaPdfDivisaoParidade pojo = getPojoParams();
    this.paridade = pojo.isPar();
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    
    int size = arquivos.size();

    progress.begin(Stage.of(this.paridade));
    
    for(int i = 0; i < size; i++) {
      Path file = Paths.get(arquivos.get(i));
      Path output = file.getParent();
      IInputFile input = new FileWrapper(file.toFile());
      InputDescriptor desc;
      try {
        desc = new PdfInputDescriptor.Builder()
          .add(input)
          .output(output)
          .build();
      } catch (IOException e1) {
        throw progress.abort(new TaskException("Não foi possível criar pasta " + output.toString()));
      }

      ByParityPdfSplitter splitter = this.paridade ? 
        new ByEvenPagesPdfSplitter() : 
        new ByOddPagesPdfSplitter();

      splitter.apply(desc).subscribe((e) -> progress.info(e.getMessage()));
    
      progress.info("Dividido arquivo %s", file);
    }
    progress.end();

    return success();
  }
}
