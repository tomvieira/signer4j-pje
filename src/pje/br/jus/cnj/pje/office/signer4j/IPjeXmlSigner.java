package br.jus.cnj.pje.office.signer4j;

import java.io.InputStream;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.exception.Signer4JException;

public interface IPjeXmlSigner extends IByteProcessor {

  ISignedData process(InputStream input) throws Signer4JException;
}
