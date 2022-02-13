package br.jus.cnj.pje.office.task.imp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaImpressao;

class PjePrintingTask extends PjeAbstractTask<ITarefaImpressao> {
  
  private static enum Stage implements IStage {
    PRINTING("Imprimindo");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private List<String> conteudo;
  
  private String impressora;
  
  protected PjePrintingTask(Params request, ITarefaImpressao pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaImpressao pojo = getPojoParams();
    this.conteudo = PjeTaskChecker.checkIfNotEmpty(pojo.getConteudo(), "conteudo");
    this.impressora = PjeTaskChecker.checkIfPresent(pojo.getImpressora(), "impressora");
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    int size = conteudo.size();
    progress.begin(Stage.PRINTING, size);
    
    try(PrintWriter printer = new PrintWriter(new FileOutputStream(impressora))) {
      int i = 0;
      do {
        String message = Strings.trim(conteudo.get(i), "empty");
        progress.step("Imprimindo conteudo[%i]:%s", i, message);
        printer.println(message);
        printer.flush();
      }while(++i < size);
    }catch(IOException e) {
      throw progress.abort(new TaskException("A impressora '" + impressora + "' está operacional?", e));
    }
    progress.end();
    return success();
  }
}
