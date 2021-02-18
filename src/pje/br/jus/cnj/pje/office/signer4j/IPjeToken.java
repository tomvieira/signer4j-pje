package br.jus.cnj.pje.office.signer4j;

import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.IToken;

public interface IPjeToken extends IToken {

  void logout(boolean force);
  
  IPjeXmlSignerBuilder xmlSignerBuilder();

  IPjeXmlSignerBuilder xmlSignerBuilder(ICertificateChooserFactory factory);
}
