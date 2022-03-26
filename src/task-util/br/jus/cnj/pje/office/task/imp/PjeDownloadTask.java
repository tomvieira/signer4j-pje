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
