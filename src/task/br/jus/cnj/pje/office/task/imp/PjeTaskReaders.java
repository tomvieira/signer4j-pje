package br.jus.cnj.pje.office.task.imp;

import java.util.function.Supplier;

import com.github.taskresolver4j.IRequestReader;
import com.github.taskresolver4j.NotImplementedReader;
import com.github.utils4j.imp.Params;

enum PjeTaskReaders implements Supplier<IRequestReader<Params>>{
  CNJ_ASSINADOR("cnj.assinador"){
    @Override
    public IRequestReader<Params> get() {
      return TarefaAssinadorReader.INSTANCE;
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
  TRF3_IMPRESSOR("trf3.impressor") {
    @Override
    public IRequestReader<Params> get() {
      return TarefaImpressaoReader.INSTANCE;
    }
  },
  SSO_AUTENTICADOR("sso.autenticador"){
    @Override
    public IRequestReader<Params> get() {
      return TarefaAutenticadorSSOReader.INSTANCE;
    }
  };

  //do not create new array's instances for each call
  private static final PjeTaskReaders[] VALUES = PjeTaskReaders.values(); 
  
  private String id;
  
  PjeTaskReaders(String id) {
    this.id = id;
  }
  
  String getId() {
    return this.id;
  }

  static IRequestReader<Params> from(String taskId) {
    for(PjeTaskReaders reader: VALUES) {
      if (reader.getId().equals(taskId))
        return reader.get();
    }
    return NotImplementedReader.INSTANCE;
  }
}
