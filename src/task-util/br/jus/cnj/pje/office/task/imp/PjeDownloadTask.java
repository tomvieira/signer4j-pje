package br.jus.cnj.pje.office.task.imp;

import static com.github.utils4j.IConstants.UTF_8;
import static com.github.utils4j.imp.Throwables.tryCall;
import static java.net.URLDecoder.decode;

import java.io.File;
import java.util.Optional;

import com.github.progress4j.IProgress;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaDownload;

class PjeDownloadTask extends PjeAbstractTask<ITarefaDownload> {
  
  private String url;
  
  private String enviarPara;
  
  protected PjeDownloadTask(Params request, ITarefaDownload pojo) {
    super(request, pojo, true);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaDownload pojo = getPojoParams();
    String urlurl = PjeTaskChecker.checkIfPresent(pojo.getUrl(), "url");
    this.url = tryCall(() -> decode(urlurl, UTF_8.name()), urlurl);
    this.enviarPara = PjeTaskChecker.checkIfPresent(pojo.getEnviarPara(), "enviarPara");
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    final IProgress progress = getProgress();
    
    progress.info("URL: %s", url);
    
    final Optional<File> downloaded = download(getExternalTarget(url), new File(enviarPara));
    
    if (!downloaded.isPresent()) {
      throw showFail("Não foi possível download do arquivo.", "URL: " + url, progress.getAbortCause());
    }
    
    showInfo("Download concluído!");
    
    progress.end();
    
    return success();
  }
}
