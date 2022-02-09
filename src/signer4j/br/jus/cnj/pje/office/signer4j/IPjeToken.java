package br.jus.cnj.pje.office.signer4j;

import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IPasswordCollector;
import com.github.signer4j.IToken;
import com.github.signer4j.imp.exception.Signer4JException;

public interface IPjeToken extends IToken {

  IPjeToken login() throws Signer4JException;
  
  IPjeToken login(IPasswordCallbackHandler callback) throws Signer4JException;
  
  IPjeToken login(IPasswordCollector collector) throws Signer4JException;

  IPjeToken login(char[] password) throws Signer4JException;

  void logout(boolean force);
  
  IPjeXmlSignerBuilder xmlSignerBuilder();

  IPjeXmlSignerBuilder xmlSignerBuilder(ICertificateChooserFactory factory);
}
