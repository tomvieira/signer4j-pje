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

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.progress4j.IQuietlyProgress;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;
import com.github.videohandler4j.IVideoFile;
import com.github.videohandler4j.imp.VideoDescriptor;
import com.github.videohandler4j.imp.VideoTools;
import com.github.videohandler4j.imp.exception.VideoDurationNotFound;

import br.jus.cnj.pje.office.task.ITarefaMedia;

abstract class PjeBasicConverterTask<T extends ITarefaMedia> extends PjeMediaProcessingTask<T> {
  
  private final String prefix;
  
  protected PjeBasicConverterTask(Params request, T pojo) {
    this(request, pojo, Strings.empty());
  }
  
  protected PjeBasicConverterTask(Params request, T pojo, String prefix) {
    super(request, pojo);
    this.prefix = prefix;
  }

  @Override
  protected boolean process(Path file, IQuietlyProgress progress) {
    
    progress.begin(SplitterStage.CONVERTING);
    
    IVideoFile video;
    try {
      video = VideoTools.FFMPEG.call(file.toFile());
    } catch (VideoDurationNotFound e) {    
      LOGGER.error("Não foi possível encontrar duração do vídeo ", e);
      progress.abort(e);
      return false;
    }

    final Path output = file.getParent();
    
    VideoDescriptor desc;
    try {
      desc = new VideoDescriptor.Builder(getExtension())
        .add(video)
        .output(output)
        .namePrefix(prefix)
        .build();
    } catch (IOException e1) {
      LOGGER.error("Não foi possível criar pasta " + output.toString(), e1);
      progress.abort(e1);
      return false;
    }
    
    AtomicBoolean success = new AtomicBoolean(true);
    
    execute(progress, desc, success);

    progress.end();

    return success.get();
  }
  
  protected String getExtension() {
    return Strings.empty();
  }
  
  protected abstract void execute(IQuietlyProgress progress, VideoDescriptor desc, AtomicBoolean success);
}
