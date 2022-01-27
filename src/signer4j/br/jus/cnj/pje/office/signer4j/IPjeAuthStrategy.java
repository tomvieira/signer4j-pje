package br.jus.cnj.pje.office.signer4j;

import com.github.signer4j.IToken;
import com.github.signer4j.imp.exception.Signer4JException;

public interface IPjeAuthStrategy {
  
  String name();
  
  void login(IToken token) throws Signer4JException ;

  void logout(IToken token);
}
