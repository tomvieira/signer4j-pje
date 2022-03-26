/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.signer4j.imp;

import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import com.github.signer4j.IToken;
import com.github.signer4j.gui.alert.TokenUseAlert;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.Signer4JException;

import br.jus.cnj.pje.office.core.imp.PjeConfig;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;


public enum PjeAuthStrategy implements IPjeAuthStrategy{
  AWAYS("Sempre solicitar senha") {
    @Override
    public void login(IToken token) throws Signer4JException {
      token.login();
    }

    @Override
    public void logout(IToken token) {
      token.logout();
    }
  },
  ONE_TIME("Solicitar senha uma vez") {
    @Override
    public void login(IToken token) throws Signer4JException {
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
    public void login(IToken token) throws Signer4JException {
      throw new LoginCanceledException();
    }
  
    @Override
    public void logout(IToken token) {
      token.logout();
    }
  },
  CONFIRM("Apenas confirmar uso do dispositivo"){
    @Override
    public void login(IToken token) throws Signer4JException {
      if (!isTrue(TokenUseAlert::display)) {
        token.logout();
        throw new LoginCanceledException();
      }
      if (!token.isAuthenticated()) { 
        token.login();
      }
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

  public static IPjeAuthStrategy getDefault() {
    return valueOf(PjeConfig.authStrategy().orElse(AWAYS.name()).toUpperCase());   
  }
}
