package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.UTIL_DOWNLOADER;

import java.io.IOException;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.ITarefaDownload;

class TarefaDownloadReader extends TarefaMediaReader<ITarefaDownload>{

  public static final TarefaDownloadReader INSTANCE = new TarefaDownloadReader();
  
  final static class TarefaDownload implements ITarefaDownload {
    private String url;
    private String enviarPara;
    
    @Override
    public final Optional<String> getUrl() {
      return Strings.optional(url);
    }

    @Override
    public Optional<String> getEnviarPara() {
      return Strings.optional(enviarPara);
    }
  }
  
  private TarefaDownloadReader() {
    super(TarefaDownload.class);
  }
  
  @Override
  protected ITask<?> createTask(Params output, ITarefaDownload pojo) throws IOException {
    return new PjeDownloadTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return UTIL_DOWNLOADER.getId();
  }

  @Override
  protected Object getTarefa(Params input) {
    TarefaDownload td = new TarefaDownload();
    td.url = input.getValue("url");
    td.enviarPara = input.getValue("enviarPara");
    return td;
  }
}
