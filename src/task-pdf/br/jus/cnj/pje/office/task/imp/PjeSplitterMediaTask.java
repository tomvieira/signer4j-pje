package br.jus.cnj.pje.office.task.imp;

import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.progress4j.imp.QuietlyProgress;
import com.github.progress4j.imp.SingleThreadProgress;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaMedia;

abstract class PjeSplitterMediaTask<T extends ITarefaMedia> extends PjeAbstractMediaTask<T> {
  
  public enum SplitterStage implements IStage {
    PROCESSING("Processando arquivos"),
    READING("Lendo o arquivo (seja paciente...)"),
    SPLITING ("Dividindo arquivos"),
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

  protected PjeSplitterMediaTask(Params request, T pojo) {
    super(request, pojo);
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    IProgress quietly =  SingleThreadProgress.wrap(QuietlyProgress.wrap(progress));
    final int size = arquivos.size();
    
    boolean success = true;
    
    progress.begin(SplitterStage.PROCESSING, size);
    for(int i = 0; i < size; i++) {
      Path file = Paths.get(arquivos.get(i));
      
      success &= process(file, quietly);
      
      progress.step("Dividido arquivo %s", file);
    }
    
    if (!success) {
      throw new TaskException("Alguns arquivos não puderam ser divididos");
    }
    
    progress.end();
    
    invokeLater(() -> display("Arquivos divididos com sucesso.", "Ótimo!"));
    return success();
  }

  protected abstract boolean process(Path file, IProgress progress);
}
