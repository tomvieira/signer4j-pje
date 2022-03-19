package br.jus.cnj.pje.office.task.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.progress4j.IQuietlyProgress;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;
import com.github.videohandler4j.IVideoFile;
import com.github.videohandler4j.imp.ByDurationVideoSplitter;
import com.github.videohandler4j.imp.VideoDescriptor;
import com.github.videohandler4j.imp.VideoTools;

import br.jus.cnj.pje.office.task.ITarefaVideoDivisaoDuracao;

class PjeByDurationVideoSplitterTask extends PjeSplitterMediaTask<ITarefaVideoDivisaoDuracao> {
  
  private long duracao;
  
  protected PjeByDurationVideoSplitterTask(Params request, ITarefaVideoDivisaoDuracao pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException, InterruptedException {
    super.validateParams();
    ITarefaVideoDivisaoDuracao pojo = getPojoParams();
    this.duracao = pojo.getDuracao();
  }
  
  @Override
  protected boolean process(Path file, IQuietlyProgress progress) {
 
    progress.begin(SplitterStage.SPLITTING_PATIENT);
    
    Path output = file.getParent();
    IVideoFile video = VideoTools.FFMPEG.call(file.toFile());
    Path folder = output.resolve(video.getShortName() + "_(VÍDEOS DE ATÉ " + duracao + " MINUTO" + (duracao > 1 ? "S)" : ")"));
    VideoDescriptor desc;
    try {
      desc = new VideoDescriptor.Builder(".mp4")
        .add(video)
        .output(folder)
        .build();
    } catch (IOException e1) {
      LOGGER.error("Não foi possível criar pasta " + output.toString(), e1);
      return false;
    }
    
    AtomicBoolean success = new AtomicBoolean(true);
    new ByDurationVideoSplitter(video, Duration.ofMinutes(duracao))
      .apply(desc)
      .subscribe(
        (e) -> progress.info(e.getMessage()),
        (e) -> {
          success.set(false);
          folder.toFile().delete();
        }
      );
    
    progress.end();

    return success.get();
  }
}
