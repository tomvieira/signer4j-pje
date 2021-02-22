package br.jus.cnj.pje.office.signer4j.imp;

import static com.github.signer4j.imp.SwingTools.isTrue;

import com.github.signer4j.IToken;
import com.github.signer4j.gui.alert.TokenUseAlert;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.LoginCanceledException;

import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;


public enum PjeAuthStrategy implements IPjeAuthStrategy{
  AWAYS("Sempre solicitar senha") {
    @Override
    public void login(IToken token) throws KeyStoreAccessException {
      token.login();
    }

    @Override
    public void logout(IToken token) {
      token.logout();
    }
  },
  ONE_TIME("Solicitar senha uma vez") {
    @Override
    public void login(IToken token) throws KeyStoreAccessException {
      if (!token.isAuthenticated()) {
        token.login();
      }
    }

    @Override
    public void logout(IToken token) {

    }
  },
  NEVER("Impedir o uso do dispositivo"){
    @Override
    public void login(IToken token) throws KeyStoreAccessException {
      throw new LoginCanceledException();
    }
  
    @Override
    public void logout(IToken token) {
      token.logout();
    }
  },
  CONFIRM("Apenas confirmar uso do dispositivo"){
    @Override
    public void login(IToken token) throws KeyStoreAccessException {
      if (token.isAuthenticated()) { 
        if (!isTrue(TokenUseAlert::display)) {
          token.logout();
          throw new LoginCanceledException();
        }
        return;
      }
      token.login();
    }
  
    @Override
    public void logout(IToken token) {
      
    }
  };
  
  private String label;
  
  PjeAuthStrategy(String message) {
    this.label = message;
  }
  
  public String geLabel() {
    return label;
  }
}
