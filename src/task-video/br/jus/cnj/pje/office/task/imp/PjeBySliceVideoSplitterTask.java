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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.Environment;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaMedia;

class PjeBySliceVideoSplitterTask extends PjeAbstractMediaTask<ITarefaMedia> {
  
  protected PjeBySliceVideoSplitterTask(Params request, ITarefaMedia pojo) {
    super(request, pojo);
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    Optional<Path> home = Environment.pathFrom("PJEOFFICE_HOME", false, true);
    if (!home.isPresent()) {
      throw showFail("Não foi encontrada a variável de ambiente PJEOFFICE_HOME");
    }
    
    Path pjeofficeHome = home.get();
    
    Path bin = pjeofficeHome.resolve("jre").resolve("bin");
    File javaw = bin.resolve("javaw.exe").toFile();
    if (!javaw.exists()) {
      javaw = bin.resolve("java").toFile(); //mac or linux
      if (!javaw.exists()) {
        throw showFail("A instalação do PJeOffice PRO encontra-se corrompida.", 
          "Não foi encontrado: " + javaw.getAbsolutePath());  
      }
    }
    
    File cutplayer = pjeofficeHome.resolve("cutplayer4jfx.jar").toFile();
    if (!cutplayer.exists()) {
      throw showFail("A instalação do PJeOffice PRO encontra-se corrompida.", 
        "Não foi encontrado: " + cutplayer.getAbsolutePath());  
    }
    
    File fileHome = pjeofficeHome.toFile();
    List<String> params = Containers.arrayList(
      javaw.getAbsolutePath(),
      "-Dpjeoffice_home=" + fileHome.getAbsolutePath(),
      "-Dffmpeg_home=" + fileHome.getAbsolutePath(),
      "-jar",
      cutplayer.getAbsolutePath()
    );
    params.addAll(arquivos);   
    
    try {
      new ProcessBuilder(params).directory(fileHome).start();      
    } catch (IOException e) {
      throw showFail("Não foi possível iniciar o player de cortes", e);
    }      

    return success();
  }
}
