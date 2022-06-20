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
import static java.net.URLEncoder.encode;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.taskresolver4j.IRequestReader;
import com.github.taskresolver4j.NotImplementedReader;
import com.github.utils4j.imp.NotImplementedException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IJsonTranslator;

public enum PjeTaskReader implements Supplier<IRequestReader<Params>>, IJsonTranslator {
  CNJ_ASSINADOR("cnj.assinador"){
    @Override
    public IRequestReader<Params> get() {
      return TarefaAssinadorReader.INSTANCE;
    }
    @Override
    public String toJson(Params input) throws Exception {
      return TarefaAssinadorReader.INSTANCE.toJson(input);
    }
  },
  CNJ_ASSINADOR_HASH("cnj.assinadorHash"){
    @Override
    public IRequestReader<Params> get() {
      return TarefaAssinadorHashReader.INSTANCE;
    }
  },
  CNJ_AUTENTICADOR("cnj.autenticador"){
    @Override
    public IRequestReader<Params> get() {
      return TarefaAutenticadorReader.INSTANCE;
    }
  },
  CNJ_ASSINADOR_BASE64("cnj.assinadorBase64") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaAssinadorBase64Reader.INSTANCE;
    }
  },
  CNJ_CERTCHAIN("cnj.certchain") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaCertChainReader.INSTANCE;
    }
  },
  SSO_AUTENTICADOR("sso.autenticador"){
    @Override
    public IRequestReader<Params> get() {
      return TarefaAutenticadorSSOReader.INSTANCE;
    }
  },
  UTIL_IMPRESSOR("util.impressor") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaImpressaoReader.INSTANCE;
    }
  },
  UTIL_DOWNLOADER("util.downloader") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaDownloadReader.INSTANCE;
    }
    
    @Override
    public String toJson(Params input) throws Exception {
      return TarefaDownloadReader.INSTANCE.toJson(input);
    }
  },
  PDF_JOIN("pdf.join") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaPdfJuncaoReader.INSTANCE;
    }
    
    @Override
    public String toJson(Params input) throws Exception {
      return TarefaPdfJuncaoReader.INSTANCE.toJson(input);
    }
    
    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".pdf");
    }
  },
  PDF_SPLIT_BY_SIZE("pdf.split_by_size") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaPdfDivisaoTamanhoReader.INSTANCE;
    }
    
    @Override
    public String toJson(Params input) throws Exception {
      return TarefaPdfDivisaoTamanhoReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".pdf");
    }
  },
  PDF_SPLIT_BY_PARITY("pdf.split_by_parity") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaPdfDivisaoParidadeReader.INSTANCE;
    }
    
    @Override
    public String toJson(Params input) throws Exception {
      return TarefaPdfDivisaoParidadeReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".pdf");
    }
  },
  PDF_SPLIT_BY_COUNT("pdf.split_by_count") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaPdfDivisaoContagemReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaPdfDivisaoContagemReader.INSTANCE.toJson(input);
    }
    
    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".pdf");
    }
  },
  PDF_SPLIT_BY_PAGES("pdf.split_by_pages") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaPdfDivisaoPaginasReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaPdfDivisaoPaginasReader.INSTANCE.toJson(input);
    }
    
    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".pdf");
    }
  },
  VIDEO_SPLIT_BY_DURATION("video.split_by_duration") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaVideoDivisaoDuracaoReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaVideoDivisaoDuracaoReader.INSTANCE.toJson(input);
    }
    
    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".mp4");
    }
  }, 
  VIDEO_SPLIT_BY_SIZE("video.split_by_size") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaVideoDivisaoTamanhoReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaVideoDivisaoTamanhoReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".mp4");
    }
  },
  VIDEO_SPLIT_BY_SLICE("video.split_by_slice") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaVideoDivisaoSliceReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaVideoDivisaoSliceReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".mp4");
    }
  },
  VIDEO_EXTRACT_AUDIO("video.extract_audio") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaVideoExtracaoAudioReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaVideoExtracaoAudioReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".mp4");
    }
  },
  VIDEO_CONVERT_WEBM("video.convert_webm") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaVideoConversaoWebmReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaVideoConversaoWebmReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".mp4");
    }
  },
  VIDEO_OPTIMIZE("video.optimize") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaVideoOtimizacaoReader.INSTANCE;
    }

    @Override
    public String toJson(Params input) throws Exception {
      return TarefaVideoOtimizacaoReader.INSTANCE.toJson(input);
    }

    @Override
    public boolean accept(File f) {
      return super.accept(f) && f.getName().toLowerCase().endsWith(".mp4");
    }
  };

  //do not create new array's instances for each call
  private static final PjeTaskReader[] VALUES = PjeTaskReader.values(); 
  
  private String id;
  
  PjeTaskReader(String id) {
    this.id = id.toLowerCase();
  }
  
  public String getId() {
    return this.id;
  }
  
  @Override
  public String toJson(Params input) throws Exception {
    throw new NotImplementedException();
  }

  public final String toUri(Params input) throws Exception {
    return "?r=" + encode(toJson(input), UTF_8.toString()) + "&u=" + System.currentTimeMillis();  
  }
  
  public static Optional<PjeTaskReader> task(String id) {
    for(PjeTaskReader reader: VALUES) {
      if (reader.getId().equalsIgnoreCase(id))
        return Optional.of(reader);
    }
    return Optional.empty();
  }
  
  static IRequestReader<Params> from(String taskId) {
    Optional<PjeTaskReader> tr = task(taskId);
    if (tr.isPresent()) {
      return tr.get().get();
    }
    return NotImplementedReader.INSTANCE;
  }

  public boolean accept(File input) {
    return input != null;
  }
}
