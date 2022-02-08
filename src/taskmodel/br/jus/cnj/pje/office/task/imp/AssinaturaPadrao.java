package br.jus.cnj.pje.office.task.imp;

import java.nio.charset.Charset;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.signer4j.IByteProcessor;
import com.github.signer4j.ICMSSigner;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Objects;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.IAssinaturaPadrao;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

enum AssinaturaPadrao implements IAssinaturaPadrao {
  ENVELOPED(".xml",  "application/xml", Constants.UTF_8) {
    @Override
    public IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params) {
      Args.requireNonNull(token, "token is null");
      return token.xmlSignerBuilder().build();
    }
  },
  
  NOT_ENVELOPED(".p7s", "application/pkcs7-signature"){
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
  
  private final String extension;
  private final String mimeType;
  private final Charset charset;
  
  private AssinaturaPadrao(String extension, String mimeType) {
    this(extension, mimeType, null);
  }

  private AssinaturaPadrao(String extension, String mimeType, Charset charset) {
    this.extension = extension;
    this.mimeType = mimeType;
    this.charset = charset;
  }

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
  
  @Override
  public String getExtension() {
    return extension;
  }

  @Override
  public String getMineType() {
    return mimeType;
  }

  @Override
  public String getCharset() {
    return Objects.toString(charset, null);
  }
}


