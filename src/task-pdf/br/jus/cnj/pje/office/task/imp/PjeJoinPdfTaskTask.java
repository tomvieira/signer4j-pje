package br.jus.cnj.pje.office.task.imp;

import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import com.github.filehandler4j.imp.InputDescriptor;
import com.github.pdfhandler4j.imp.JoinPdfHandler;
import com.github.pdfhandler4j.imp.PdfInputDescriptor;
import com.github.pdfhandler4j.imp.PdfInputDescriptor.Builder;
import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Dates;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaMedia;

class PjeJoinPdfTaskTask extends PjeAbstractMediaTask<ITarefaMedia> {
  
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
  
  protected PjeJoinPdfTaskTask(Params request, ITarefaMedia pojo) {
    super(request, pojo);
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    int size = arquivos.size();
    //são três passos por arquivo: (1:inicio leitura + 1:fim leitura + 1:mesclagem) + 1:geração do arquivo final!
    progress.begin(Stage.MERGING, 3 * size + 1);

    AtomicReference<Path> parent = new AtomicReference<>();
    Builder builder = new PdfInputDescriptor.Builder();
    arquivos.stream()
      .sorted()
      .map(s -> Paths.get(s))
      .peek(p -> parent.set(p.getParent()))
      .forEach(path -> builder.add(path.toFile()));
    Path output = parent.get();
    
    InputDescriptor desc;
    try {
      desc = builder.output(output).build();
    } catch (IOException e) {
      throw progress.abort(new TaskException("Não foi possível gerar arquivo de saída. Permissão?", e));
    }
    
    new JoinPdfHandler("ARQUIVOS_UNIDOS_EM_" + Dates.stringNow())
      .apply(desc)
      .subscribe(
        (e) -> progress.step(e.getMessage()),
        (e) -> progress.abort(e)
      );
    
    Throwable fail = progress.getAbortCause();
    if (fail != null) {
      invokeLater(() -> display("Não foi possível unir os arquivos.\n" + fail.getMessage()));
      return fail(fail);
    }
    
    progress.end();
    invokeLater(() -> display("Arquivos unidos com sucesso.", "Ótimo!"));
    return success();    
  }
}
