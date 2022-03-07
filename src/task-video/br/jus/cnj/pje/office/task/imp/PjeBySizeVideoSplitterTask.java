package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;
import com.github.videohandler4j.IVideoFile;
import com.github.videohandler4j.imp.BySizeVideoSplitter;
import com.github.videohandler4j.imp.VideoDescriptor;
import com.github.videohandler4j.imp.VideoTool;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoTamanho;

public class PjeBySizeVideoSplitterTask extends PjeAbstractMediaTask<ITarefaVideoDivisaoTamanho> {
  
  private static enum Stage implements IStage {
    SPLITING ("Dividindo arquivos");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private long tamanho;
  
  protected PjeBySizeVideoSplitterTask(Params request, ITarefaVideoDivisaoTamanho pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    ITarefaVideoDivisaoTamanho pojo = getPojoParams();
    this.tamanho = pojo.getTamanho();
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    IProgress progress = getProgress();
    final int size = arquivos.size();
    
    progress.begin(Stage.SPLITING);

    for(int i = 0; i < size; i++) {
      Path file = Paths.get(arquivos.get(i));
      Path output = file.getParent();
      IVideoFile video = VideoTool.FFMPEG.call(file.toFile());
      
      VideoDescriptor desc;
      try {
        desc = new VideoDescriptor.Builder(".mp4")
          .add(video)
          .output(output.resolve(video.getShortName() + "_(CORTES DE ATÉ " + tamanho + " MB)"))
          .build();
      } catch (IOException e1) {
        throw progress.abort(new TaskException("Não foi possível criar pasta " + output.toString()));
      }
      
      new BySizeVideoSplitter(video, tamanho * 1024 * 1024)
        .apply(desc)
        .subscribe((e) -> progress.info(e.getMessage()));
    
      progress.info("Dividido arquivo %s", file.toString());
    }
    progress.end();
    return success();
  }
}
