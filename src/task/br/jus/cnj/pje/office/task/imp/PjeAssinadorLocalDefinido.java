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

import static com.github.utils4j.imp.Throwables.tryCall;
import static java.net.URLDecoder.decode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Dates;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IArquivo;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

class PjeAssinadorLocalDefinido extends PjeAssinadorLocalTask {

  private static enum Stage implements IStage {
    SELECTING_FILE; 
    
    @Override
    public String toString() {
      return "Selecionando arquivos";
    }
  }
  
  private List<IArquivo> arquivos;
  
  private String enviarPara;

  PjeAssinadorLocalDefinido(Params request, ITarefaAssinador pojo) {
    super(request, pojo);
  }
  
  @Override
  protected void validateTaskParams() throws TaskException {
    super.validateTaskParams();
    final ITarefaAssinador pojo = getPojoParams();
    this.arquivos = PjeTaskChecker.checkIfNotEmpty(pojo.getArquivos(), "arquivos");
    this.enviarPara = PjeTaskChecker.checkIfPresent(pojo.getEnviarPara(), "enviarPara");
  }

  @Override
  protected IArquivoAssinado[] selectFiles() throws TaskException, InterruptedException {

    final int size = arquivos.size();
    
    final List<File> inputFiles = new ArrayList<>();
    
    final IProgress progress  = getProgress();
    
    progress.begin(Stage.SELECTING_FILE, size);
    
    int i = 0;
    do {
      final IArquivo arquivo = arquivos.get(i);
      final Optional<String> oUrl = arquivo.getUrl();
      if (!oUrl.isPresent()) {
        LOGGER.warn("Detectado arquivo com caminho vazio");
        progress.step("Decartado arquivo com url vazia");
        continue;
      }
      final File file = new File(tryCall(() -> decode(oUrl.get(), IConstants.UTF_8.name()), oUrl.get()));
      if (!file.exists()) {
        String fullPath = file.getAbsolutePath();
        LOGGER.warn("Detectado arquivo com caminho inexistente {}", fullPath);
        progress.step("Descartado arquivo não localizado '%s'", fullPath);
        continue;
      }
      progress.step("Selecionando arquivo: %s", file);
      inputFiles.add(file);
    }while(++i < size);
    
    progress.end();
    return super.collectFiles(inputFiles.toArray(new File[inputFiles.size()]));
  }
  

  @Override
  protected File chooseDestination() throws InterruptedException {
    if ("selectfolder".equals(enviarPara)) {
      return super.chooseDestination();
    }
    
    File folderReference;
    Optional<IArquivoAssinado> primeiro = super.getFirst();
    if (primeiro.isPresent()) {
      folderReference = new File(primeiro.get().getUrl().get()).getParentFile(); 
    } else {
      return super.chooseDestination();
    }
    
    if ("newfolder".equals(enviarPara)) {
      folderReference = new File(folderReference, "ASSINADOS_EM_" + Dates.stringNow());
      folderReference.mkdirs();
    } 
    
    do {
      if (folderReference.exists() && folderReference.canWrite())
        return folderReference;
      showCanNotWriteMessage(folderReference);
      folderReference = super.chooseDestination();
    }while(true);
  }
}
