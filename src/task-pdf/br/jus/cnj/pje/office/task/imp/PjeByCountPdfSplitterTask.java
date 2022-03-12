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
import com.github.pdfhandler4j.imp.ByCountPdfSplitter;
import com.github.pdfhandler4j.imp.BySinglePagePdfSplitter;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.pdfhandler4j.imp.event.PdfEndEvent;
import com.github.pdfhandler4j.imp.event.PdfOutputEvent;
import com.github.pdfhandler4j.imp.event.PdfPageEvent;
import com.github.pdfhandler4j.imp.event.PdfReadingEnd;
import com.github.pdfhandler4j.imp.event.PdfReadingStart;
import com.github.pdfhandler4j.imp.event.PdfStartEvent;
import com.github.progress4j.IProgress;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoContagem;

class PjeByCountPdfSplitterTask extends PjeSplitterMediaTask<ITarefaPdfDivisaoContagem> {
  
  private long totalPaginas;
  
  protected PjeByCountPdfSplitterTask(Params request, ITarefaPdfDivisaoContagem pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    this.totalPaginas = getPojoParams().getTotalPaginas();
    if (this.totalPaginas <= 0) {
      Optional<Integer> total = ofNullable(getInteger(
        "Número máximo de páginas:", 
        10, 
        1, 
        Integer.MAX_VALUE - 1
      ));
      this.totalPaginas = total.orElseThrow(() -> new TaskException(IProgress.CANCELED_OPERATION_MESSAGE));
    }
  }
  
  @Override
  protected boolean process(Path file, IProgress progress) {
    Path parentFolder = file.getParent();
    IInputFile input = new FileWrapper(file.toFile());
    InputDescriptor desc;
    Path outputFolder = parentFolder.resolve(input.getShortName() + 
        "_(VOLUMES DE " + this.totalPaginas + " PÁGINA" + (totalPaginas > 1 ? "S)" : ")"));
    try {
      desc = new PdfInputDescriptor.Builder()
        .add(input)
        .output(outputFolder)
        .build();
    } catch (IOException e1) {
      LOGGER.error("Não foi possível criar pasta " + parentFolder.toString(), e1);
      return false;
    }

    ByCountPdfSplitter splitter = this.totalPaginas == 1 ? 
      new BySinglePagePdfSplitter() : 
      new ByCountPdfSplitter(totalPaginas);

    AtomicBoolean success = new AtomicBoolean(true);
    
    splitter.apply(desc).subscribe(
      e -> {
        if (e instanceof PdfReadingStart) {
          progress.begin(SplitterStage.READING);
        } else if (e instanceof PdfStartEvent) {
          progress.begin(SplitterStage.SPLITING, ((PdfStartEvent)e).getTotalPages());            
        } else if (e instanceof PdfReadingEnd || e instanceof PdfEndEvent) {
          progress.end();
        } else if (e instanceof PdfPageEvent) {
          progress.step(e.getMessage());
        } else if (e instanceof PdfOutputEvent && totalPaginas == 1){
          progress.step(e.getMessage());
        } else {
          progress.info(e.getMessage());  
        }
      },
      e -> {
        success.set(false);
        outputFolder.toFile().delete();
      }
    ); 
    return success.get();
  }
}
