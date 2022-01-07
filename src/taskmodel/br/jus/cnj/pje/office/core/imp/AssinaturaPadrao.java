package br.jus.cnj.pje.office.core.imp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IByteProcessor;
import com.github.signer4j.ICMSSigner;
import com.github.signer4j.imp.Args;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.ITarefaAssinador;
import br.jus.cnj.pje.office.core.IAssinaturaPadrao;
import br.jus.cnj.pje.office.signer4j.IPjeToken;

enum AssinaturaPadrao implements IAssinaturaPadrao {
  ENVELOPED() {
    @Override
    public IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params) {
      Args.requireNonNull(token, "token is null");
      return token.xmlSignerBuilder().build();
    }
  },
  
  NOT_ENVELOPED(){
    @Override
    public IAssinaturaPadrao checkIfDependentParamsIsPresent(ITarefaAssinador params) throws TaskException  {
      PjeTaskChecker.checkIfNull(params, "params is null");
      PjeTaskChecker.checkIfPresent(params.getAlgoritmoHash(), "algoritmoHash");
      PjeTaskChecker.checkIfPresent(params.getTipoAssinatura(), "tipoAssinatura");
      return this;
    }

    @Override
    public IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params) {
      Args.requireNonNull(token, "token is null");
      Args.requireNonNull(params, "param is null");
      return token.cmsSignerBuilder()
        .usingSignatureAlgorithm(params.getAlgoritmoHash().get())
        .usingSignatureType(params.getTipoAssinatura().get())
        .usingConfig((p, o) -> ((ICMSSigner)p).usingAttributes((Boolean)o))
        .build();
    }
  }; 

  @Override
  public IAssinaturaPadrao checkIfDependentParamsIsPresent(ITarefaAssinador params) throws TaskException  {
    return this;
  }
  
  @JsonCreator
  public static AssinaturaPadrao fromString(final String key) {
    return key == null ? NOT_ENVELOPED : valueOf(key.toUpperCase());
  }

  @JsonValue
  public String getKey() {
    return this.name().toLowerCase();
  }
}


