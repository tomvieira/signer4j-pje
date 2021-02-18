package br.jus.cnj.pje.office.core.imp;

import java.util.function.Supplier;

import com.github.signer4j.imp.Params;
import com.github.signer4j.task.IRequestReader;
import com.github.signer4j.task.NotImplementedReader;

enum PjeRequestReaders implements Supplier<IRequestReader<Params>>{
  CNJ_ASSINADOR("cnj.assinador"){
    @Override
    public IRequestReader<Params> get() {
      return PjeAssinadorReader.INSTANCE;
    }
  },
  CNJ_ASSINADOR_HASH("cnj.assinadorHash"){
    @Override
    public IRequestReader<Params> get() {
      return PjeAssinadorHashReader.INSTANCE;
    }
  },
  CNJ_AUTENTICADOR("cnj.autenticador"){
    @Override
    public IRequestReader<Params> get() {
      return PjeAutenticadorReader.INSTANCE;
    }
  },
  SSO_AUTENTICADOR("sso.autenticador"){
    @Override
    public IRequestReader<Params> get() {
      return NotImplementedReader.INSTANCE; //we have to go back here!
    }
  };

  //do not create new array's instances for each call
  private static final PjeRequestReaders[] VALUES = PjeRequestReaders.values(); 
  
  private String id;
  
  PjeRequestReaders(String id) {
    this.id = id;
  }
  
  String getId() {
    return this.id;
  }

  static IRequestReader<Params> from(String taskId) {
    for(PjeRequestReaders reader: VALUES) {
      if (reader.getId().equals(taskId))
        return reader.get();
    }
    return NotImplementedReader.INSTANCE;
  }
}
