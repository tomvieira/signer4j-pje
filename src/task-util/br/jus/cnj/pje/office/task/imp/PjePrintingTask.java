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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;
import com.github.videohandler4j.imp.TimeTools;

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
  
  private String porta;
  
  protected PjePrintingTask(Params request, ITarefaImpressao pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateTaskParams() throws TaskException, InterruptedException {
    ITarefaImpressao pojo = getPojoParams();
    this.conteudo = PjeTaskChecker.checkIfNotEmpty(pojo.getConteudo(), "conteudo");
    this.porta = PjeTaskChecker.checkIfPresent(pojo.getPorta(), "porta");
  }

  
  @Override
  protected void onBeforeDoGet() throws TaskException, InterruptedException {
    //Dificulta ataques DDO's de impressão por fishing!
    Dialogs.Choice choice = Dialogs.getBoolean(
      "Confirma a impressão de dados?",
      "Confirmação", 
      false
    );
    if (choice != Dialogs.Choice.YES) {
      throwCancel(false);
    }
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    int size = conteudo.size();
    progress.begin(Stage.PRINTING, 2 * size);
    try(PrintWriter printer = new PrintWriter(new FileOutputStream(porta))) {
      int i = 0;
      do {
        String message = Strings.trim(conteudo.get(i), "empty");
        progress.step("Enviando conteúdo [%s]:%s", i, message);
        progress.info("Aguardando resposta da porta '%s' (seja paciente...)", porta);
        long start = System.currentTimeMillis();
        printer.println(message);
        printer.flush();
        long time = System.currentTimeMillis() - start;
        if (time > TimeTools.ONE_MINUTE.toMillis()) {
          throw new IOException("Há uma demora incomum de mais de 60 " +
            " segundos para liberação de escrita na porta '" + porta + "'. Isto sugere que a "
              + "impressora está desligada, ocupada ou a referida porta não está configurada "
              + "adequadamente."); 
        }
        progress.step("Conteúdo enviado em %s ms", time);
      }while(++i < size);
    }catch(IOException e) {
      throw progress.abort(showFail("A porta '" + porta + "' não está acessível.", e));
    }
    progress.end();
    return success();
  }
}
