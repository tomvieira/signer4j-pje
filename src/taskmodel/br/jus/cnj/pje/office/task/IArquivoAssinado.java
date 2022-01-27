package br.jus.cnj.pje.office.task;

import java.io.IOException;
import java.util.Optional;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.exception.Signer4JException;

import br.jus.cnj.pje.office.core.imp.UnsupportedCosignException;

public interface IArquivoAssinado extends IArquivo {
  Optional<ISignedData> getSignedData();
  
  void sign(IByteProcessor signer) throws Signer4JException, IOException, UnsupportedCosignException;
  
  String getFileFieldName();
}