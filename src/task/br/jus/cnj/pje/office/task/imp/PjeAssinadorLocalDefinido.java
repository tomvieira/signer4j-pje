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

public class PjeAssinadorLocalDefinido extends PjeAssinadorLocalTask {

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
  protected void validateParams() throws TaskException {
    super.validateParams();
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
