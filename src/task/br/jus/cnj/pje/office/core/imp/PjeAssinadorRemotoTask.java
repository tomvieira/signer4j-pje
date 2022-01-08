package br.jus.cnj.pje.office.core.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.DownloadStatus;
import com.github.signer4j.imp.Params;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.IArquivo;
import br.jus.cnj.pje.office.core.IArquivoAssinado;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.ITarefaAssinador;

class PjeAssinadorRemotoTask extends PjeAssinadorTask {
  
  private static enum Stage implements IStage {
    DOWNLOADING_FILE; 
    
    @Override
    public String toString() {
      return "Download de arquivos";
    }
  }
  
  private final List<IArquivoAssinado> tempFiles = new ArrayList<>();
  
  private List<IArquivo> arquivos;
  
  private String enviarPara;
  
  PjeAssinadorRemotoTask(Params request, ITarefaAssinador pojo) {
    super(request, pojo);
  }
  
  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    final ITarefaAssinador params = getPojoParams();
    this.arquivos = PjeTaskChecker.checkIfNull(params.getArquivos(), "arquivos");
    this.enviarPara = PjeTaskChecker.checkIfPresent(params.getEnviarPara(), "enviarPara");
  }

  @Override
  protected IArquivoAssinado[] selectFiles() throws TaskException {
    if (arquivos.isEmpty()) {
      throw new TaskException("A requisição não informou qualquer URL para download dos arquivos");
    }
    
    final int size = arquivos.size();
    
    final IPjeClient client   = getPjeClient();
    final String session      = getSession();
    final String userAgent    = getUserAgent();
    final IProgress progress  = getProgress();
    
    progress.begin(Stage.DOWNLOADING_FILE, size);
    
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
      final String endPoint = getEndpointFor(url);
      
      progress.step("Baixando url: %s", endPoint);
      final DownloadStatus status = new DownloadStatus(progress);
      try {
        client.down(endPoint, session, userAgent, status);
      } catch (PjeServerException e) {
        TaskException ex = new TaskException("Não foi possível realizar o download de " + url);
        progress.abort(ex);
        throw ex;
      }
      tempFiles.add(new ArquivoAssinado(arquivo, status.getDownloadedFile()) {
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
  protected void send(IArquivoAssinado arquivo) throws TaskException {
    final String endPoint = getEndpointFor(enviarPara);
    try {
      getPjeClient().send(
        endPoint, 
        getSession(), 
        getUserAgent(), 
        arquivo
      );
    } catch (PjeServerException e) {
      throw new TaskException("Não foi possível enviar o arquivo para o servidor: " + endPoint);
    }
  }
}
