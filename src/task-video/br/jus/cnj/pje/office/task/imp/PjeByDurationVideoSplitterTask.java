package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.progress4j.imp.SingleThreadProgress;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;
import com.github.videohandler4j.IVideoFile;
import com.github.videohandler4j.imp.ByDurationVideoSplitter;
import com.github.videohandler4j.imp.VideoDescriptor;
import com.github.videohandler4j.imp.VideoTool;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoDuracao;

class PjeByDurationVideoSplitterTask extends PjeAbstractMediaTask<ITarefaVideoDivisaoDuracao> {
  
  private static enum Stage implements IStage {
    SPLITING("Dividindo o vídeo");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private long duracao;
  
  protected PjeByDurationVideoSplitterTask(Params request, ITarefaVideoDivisaoDuracao pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    ITarefaVideoDivisaoDuracao pojo = getPojoParams();
    this.duracao = pojo.getDuracao();
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    final IProgress progress = SingleThreadProgress.wrap(getProgress());
    
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
          .output(output.resolve(video.getShortName() + "_(VÍDEOS DE ATÉ " + duracao + " MINUTO" + (duracao > 1 ? "S)" : ")")))
          .build();
      } catch (IOException e1) {
        throw progress.abort(new TaskException("Não foi possível criar pasta " + output.toString()));
      }
      
      new ByDurationVideoSplitter(video, Duration.ofMinutes(duracao))
        .apply(desc)
        .subscribe((e) -> progress.info(e.getMessage()));
    
      progress.info("Dividido arquivo %s", file);
    }
    
    progress.end();

    return success();
  }
}
