package br.jus.cnj.pje.office.task.imp;

import static com.github.utils4j.IConstants.UTF_8;
import static java.net.URLEncoder.encode;

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
  PDF_JOIN("pdf.join") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaPdfJuncaoReader.INSTANCE;
    }
    
    @Override
    public String toJson(Params input) throws Exception {
      return TarefaPdfJuncaoReader.INSTANCE.toJson(input);
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
  };


  //do not create new array's instances for each call
  private static final PjeTaskReader[] VALUES = PjeTaskReader.values(); 
  
  private String id;
  
  PjeTaskReader(String id) {
    this.id = id;
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
  
  static IRequestReader<Params> from(String taskId) {
    for(PjeTaskReader reader: VALUES) {
      if (reader.getId().equals(taskId))
        return reader.get();
    }
    return NotImplementedReader.INSTANCE;
  }
  
}
