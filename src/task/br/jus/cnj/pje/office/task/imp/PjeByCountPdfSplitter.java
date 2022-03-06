package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.FileWrapper;
import com.github.filehandler4j.imp.InputDescriptor;
import com.github.pdfhandler4j.imp.ByCountPdfSplitter;
import com.github.pdfhandler4j.imp.BySinglePagePdfSplitter;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoContagem;

class PjeByCountPdfSplitter extends PjeAbstractPdfTask<ITarefaPdfDivisaoContagem> {
  
  private static enum Stage implements IStage {
    SPLITING("Dividindo o arquivo");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private long totalPaginas;
  
  protected PjeByCountPdfSplitter(Params request, ITarefaPdfDivisaoContagem pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    ITarefaPdfDivisaoContagem pojo = getPojoParams();
    this.totalPaginas = pojo.getTotalPaginas();
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    
    int size = arquivos.size();

    progress.begin(Stage.SPLITING);
    
    for(int i = 0; i < size; i++) {
      Path file = Paths.get(arquivos.get(i));
      Path output = file.getParent();
      IInputFile input = new FileWrapper(file.toFile());
      InputDescriptor desc;
      try {
        desc = new PdfInputDescriptor.Builder()
          .add(input)
          .output(output.resolve(input.getShortName() + "_(VOLUMES DE " + 
              this.totalPaginas + " PÁGINA" + (totalPaginas > 1 ? "S)" : ")")))
          .build();
      } catch (IOException e1) {
        throw progress.abort(new TaskException("Não foi possível criar pasta " + output.toString()));
      }

      ByCountPdfSplitter splitter = this.totalPaginas == 1 ? 
        new BySinglePagePdfSplitter() : 
        new ByCountPdfSplitter(totalPaginas);

      splitter.apply(desc).subscribe((e) -> progress.info(e.getMessage()));
    
      progress.info("Dividido arquivo %s", file);
    }
    progress.end();

    return success();
  }
}
