package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
import br.jus.cnj.pje.office.task.ITarefaPdfJuncao;

class PjeJoinPdfTask extends PjeAbstractTask<ITarefaPdfJuncao> {
  
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
  
  private List<String> arquivos;
  
  protected PjeJoinPdfTask(Params request, ITarefaPdfJuncao pojo) {
    super(request, pojo, true);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaPdfJuncao pojo = getPojoParams();
    this.arquivos = PjeTaskChecker.checkIfNotEmpty(pojo.getArquivos(), "arquivos");
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    int size = arquivos.size();
    progress.begin(Stage.MERGING, 3 * size + 1); //são três passos por arquivo: 3*(inicio leitura, fim leitura e mesclagem) + 1 geração do arquivo!

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
    new JoinPdfHandler("resultado_uniao_em_" + Dates.stringNow())
      .apply(desc)
      .subscribe((e) -> progress.step(e.getMessage()));
    progress.end();
    return success();
  }
}
