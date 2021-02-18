package br.jus.cnj.pje.office.core;

import java.util.List;
import java.util.Optional;

import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignatureType;

public interface IAssinadorParams {
  
  String PJE_TAREFA_ASSINADOR_PARAM = IAssinadorParams.class.getSimpleName();
  
  List<IArquivo> getArquivos();

  Optional<String> getEnviarPara();

  Optional<ISignerMode> getModo();

  Optional<ISignatureType> getTipoAssinatura();

  boolean isDeslogarKeyStore(); //TODO ignorar isso?

  Optional<ISignatureAlgorithm> getAlgoritmoHash();

  Optional<IStandardSignature> getPadraoAssinatura();
}