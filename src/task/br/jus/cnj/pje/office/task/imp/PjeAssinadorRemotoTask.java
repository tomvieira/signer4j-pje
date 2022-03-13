package br.jus.cnj.pje.office.task.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.task.IArquivo;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IPjeTarget;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

class PjeAssinadorRemotoTask extends PjeAssinadorTask {
  
  private static enum Stage implements IStage {
    DOWNLOADING_FILES; 
    
    @Override
    public String toString() {
      return "Download dos arquivos";
    }
  }
  
  private final List<IArquivoAssinado> tempFiles = new ArrayList<>();
  
  private List<IArquivo> arquivos;
  
  private String enviarPara;
  
  PjeAssinadorRemotoTask(Params request, ITarefaAssinador pojo) {
    super(request, pojo, false);
  }
  
  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    final ITarefaAssinador params = getPojoParams();
    this.arquivos = PjeTaskChecker.checkIfNull(params.getArquivos(), "arquivos");
    this.enviarPara = PjeTaskChecker.checkIfPresent(params.getEnviarPara(), "enviarPara");
  }

  @Override
  protected IArquivoAssinado[] selectFiles() throws TaskException, InterruptedException {
    if (arquivos.isEmpty()) {
      throw new TaskException("A requisição não informou qualquer URL para download dos arquivos");
    }
    
    final int size = arquivos.size();
    
    final IProgress progress  = getProgress();
    
    progress.begin(Stage.DOWNLOADING_FILES, size);
    
    int i = 0;
    do {
      final IArquivo arquivo = arquivos.get(i);
      final Optional<String> oUrl = arquivo.getUrl();
      if (!oUrl.isPresent()) {
        LOGGER.warn("Detectado arquivo com URL para download VAZIA");
        progress.step("Decartado arquivo com url vazia");
        continue;
      }
      final String url = oUrl.get();
      
      final IPjeTarget target = getTarget(url);
      
      progress.step("URL: %s", target.getEndPoint());

      final DownloadStatus status = download(target);
      
      tempFiles.add(new ArquivoAssinado(arquivo, status.getDownloadedFile().get()) {
        @Override
        public void dispose() {
          super.dispose();
          super.notSignedFile.delete(); //this is temporary downloaded files!
        }
      });
    }while(++i < size);
    
    progress.end();
    return tempFiles.toArray(new IArquivoAssinado[tempFiles.size()]);
  }

  @Override
  public void dispose() {
    tempFiles.forEach(IArquivo::dispose);
    tempFiles.clear();
  }
  
  @Override
  protected PjeTaskResponse send(IArquivoAssinado arquivo) throws TaskException, InterruptedException {
    Args.requireNonNull(arquivo, "arquivo is null");
    IPjeTarget target = getTarget(enviarPara);
    try {
      return getPjeClient().send(
        target,
        arquivo,
        padraoAssinatura
      );
    } catch (PJeClientException e) {
      throw new TaskException("Não foi possível enviar o arquivo para o servidor: " + target.getEndPoint());
    }
  }
}
