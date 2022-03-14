package br.jus.cnj.pje.office.task.imp;

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
import com.github.signer4j.imp.Config;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaMedia;
import test.FileListWindow;

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
    
    progress.info("Aguardando ordenação dos arquivos");
    
    AtomicReference<Path> parent = new AtomicReference<>();

    List<File> files = arquivos.stream()
        .filter(Strings::hasText)
        .map(s -> new File(s))
        .filter(File::exists)
        .peek(f -> parent.set(f.toPath().getParent()))
        .collect(toList());
      
    Optional<String> fileName = new FileListWindow(Config.getIcon(), files).getFileName();
    
    if (!fileName.isPresent()) {
      throw new InterruptedException();
    }
    
    progress.info("Arquivos ordenados");

    int size = files.size();

    Builder builder = new PdfInputDescriptor.Builder();
    
    files.forEach(f -> builder.add(f));
    
    Path output = parent.get();
    
    progress.begin(Stage.MERGING, 3 * size + 1);

    InputDescriptor desc;
    try {
      desc = builder.output(output).build();
    } catch (IOException e) {
      throw progress.abort(new TaskException("Não foi possível gerar arquivo de saída. Permissão?", e));
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
    
    progress.info("Unidos " + size + " arquivos"); 
    
    if (!success.get()) {
      throw new TaskException("Não foi possível unir os arquivos.\n", progress.getAbortCause());
    }
    
    progress.end();
   
    showInfo("Arquivos unidos com sucesso.", "Ótimo!");
    return success();    
  }
}
