package br.jus.cnj.pje.office.signer4j;

import com.github.signer4j.IToken;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

public interface IPjeAuthStrategy {
  
  String name();
  
  void login(IToken token) throws KeyStoreAccessException ;

  void logout(IToken token);
}
