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

public class PjeBySliceVideoSplitterTask extends PjeAbstractMediaTask<ITarefaMedia> {
  
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
    
    File javaw = pjeofficeHome.resolve("jre").resolve("bin").resolve("javaw.exe").toFile();
    if (!javaw.exists()) {
      throw showFail("A instalação do PJeOffice PRO encontra-se corrompida.", 
        "Não foi encontrado: " + javaw.getAbsolutePath());  
    }
    
    File cutplayer = pjeofficeHome.resolve("cutplayer4jfx.jar").toFile();
    if (!cutplayer.exists()) {
      throw showFail("A instalação do PJeOffice PRO encontra-se corrompida.", 
        "Não foi encontrado: " + cutplayer.getAbsolutePath());  
    }
    
    List<String> params = Containers.arrayList(
      javaw.getAbsolutePath(),
      "-jar",
      cutplayer.getAbsolutePath()
    );
    params.addAll(arquivos);    
    
    try {
      new ProcessBuilder(params).directory(pjeofficeHome.toFile()).start();      
    } catch (IOException e) {
      throw showFail("Não foi possível iniciar iniciar o player de cortes", e);
    }      

    return success();
  }
}
