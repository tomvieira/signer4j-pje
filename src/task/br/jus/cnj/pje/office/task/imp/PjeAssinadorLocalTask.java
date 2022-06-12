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

import static br.jus.cnj.pje.office.task.imp.TarefaAssinadorReader.AssinadorArquivo.newInstance;
import static com.github.progress4j.IProgress.CANCELED_OPERATION_MESSAGE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import javax.swing.JFileChooser;

import com.github.progress4j.IProgressView;
import com.github.signer4j.ISignedData;
import com.github.signer4j.gui.alert.MessageAlert;
import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.gui.imp.DefaultFileChooser;
import com.github.utils4j.gui.imp.ExceptionAlert;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

class PjeAssinadorLocalTask extends PjeAssinadorTask {
  
  private static final String PJE_DESTINATION_PARAM = "PjeAssinadorLocalTask.destinationDir";

  PjeAssinadorLocalTask(Params request, ITarefaAssinador pojo) {
    super(request, pojo, true);
  }

  @Override
  protected void validateTaskParams() throws TaskException {
    super.validateTaskParams();
    //insert here new validations
  }

  @Override
  protected final ITaskResponse<IPjeResponse> doGet() throws TaskException {
    runAsync(() -> {
      IProgressView progress = newProgress();      
      try {
        progress.display();
        super.doGet();
      } catch(InterruptedException | InterruptedSigner4JRuntimeException e ) {
        MessageAlert.showFail(CANCELED_OPERATION_MESSAGE);
      } catch(TaskException e) {
        LOGGER.error("Falha na execução da tarefa", e);
      } catch(Throwable e) {
        String message = "Houve uma falha inexperada durante o processo de assinatura!";
        LOGGER.warn(message, e);
        ExceptionAlert.show(message, e);
      } finally {
        progress.undisplay();
        progress.stackTracer(s -> LOGGER.info(s.toString()));
        progress.dispose();
      }
    });
    return success();
  }

  @Override
  protected IArquivoAssinado[] selectFiles() throws TaskException, InterruptedException {
    return collectFiles(selectFilesFromDialogs("Selecione o(s) arquivo(s) a ser(em) assinado(s)"));
  }

  protected IArquivoAssinado[] collectFiles(File[] files) throws TaskException {
    int size;
    if (files == null || (size = files.length) == 0) {
      throw new TaskException("Nenhum arquivo selecionado");
    }
    IArquivoAssinado[] filesToSign = new IArquivoAssinado[size];
    int i = 0;
    for(File file: files) {
      filesToSign[i++]= new ArquivoAssinado(newInstance(file, getFileNamePrefix()), file);
    }
    return filesToSign;
  }
  
  protected String getFileNamePrefix() {
    return Strings.empty(); //".ASSINADO_EM_" + stringNow();
  }
  
  protected File chooseDestination() throws InterruptedException {
    final JFileChooser chooser = new DefaultFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle("Selecione onde será(ão) gravado(s) o(s) arquivo(s) assinado(s)");
    switch(chooser.showOpenDialog(null)) {
      case JFileChooser.APPROVE_OPTION:
        return chooser.getSelectedFile(); 
      default:
        throwCancel();
        return null;
    }
  }

  @Override
  protected PjeTaskResponse send(IArquivoAssinado arquivo) throws TaskException, InterruptedException {
    Args.requireNonNull(arquivo, "arquivo is null");
    
    Optional<ISignedData> signature = arquivo.getSignedData();
    if (!signature.isPresent()) {
      throw new TaskException("Arquivo não foi assinado!");
    }
    
    final ISignedData signedData = signature.get();
    
    File destination;
    do {
      destination = params.isPresent(PJE_DESTINATION_PARAM) ?  
        params.getValue(PJE_DESTINATION_PARAM) : 
        chooseDestination();
      if (destination.canWrite()) 
        break;
      showCanNotWriteMessage(destination);
      params.of(PJE_DESTINATION_PARAM, Optional.empty());
    }while(true);
      
    params.of(PJE_DESTINATION_PARAM, destination);
    
    final String fileName = arquivo.getNome().get();

    final File saved = new File(destination, fileName);
    try(OutputStream output = new FileOutputStream(saved)){ 
      signedData.writeTo(output);
    } catch (IOException e) {
      saved.delete();
      throw new TaskException("Não foi possível salvar o arquivo assinado.", e);
    }
    return success(arquivo.getUrl().get());
  }

  protected void showCanNotWriteMessage(File destination) {
    showInfo("Não há permissão de escrita na pasta:\n" + destination.getAbsolutePath() + "\nEscolha uma nova!");
  }
}
