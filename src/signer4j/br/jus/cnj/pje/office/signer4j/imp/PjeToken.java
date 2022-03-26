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

import static com.github.utils4j.imp.Args.requireNonNull;

import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.IPKCS7SignerBuilder;
import com.github.signer4j.IPasswordCallbackHandler;
import com.github.signer4j.IPasswordCollector;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.NotAuthenticatedException;
import com.github.signer4j.imp.TokenWrapper;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.gui.certlist.PjeCertificateListAcessor;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.signer4j.IPjeXmlSignerBuilder;

public class PjeToken extends TokenWrapper implements IPjeToken {
  
  private static final ICertificateChooserFactory PJE = (k, c) -> new PjeCertificateListAcessor(k, c);
  
  private final Runnable DISPOSE_ACTION = () -> logout(true);
  
  private final IPjeAuthStrategy strategy;
  
  public PjeToken(IToken token, IPjeAuthStrategy strategy) {
    super(token);
    this.strategy = Args.requireNonNull(strategy, "strategy is null");
  }

  private void checkIfAvailable() {
    if (!isAuthenticated()) {
      throw new NotAuthenticatedException("Unabled to prepare signer with no authenticated token");
    }
  }
  
  @Override
  public final ISignerBuilder signerBuilder() {
    return super.signerBuilder(PJE);
  }
  
  @Override
  public final ICMSSignerBuilder cmsSignerBuilder() {
    return super.cmsSignerBuilder(PJE);
  }

  @Override
  public final IPKCS7SignerBuilder pkcs7SignerBuilder() {
    return super.pkcs7SignerBuilder(PJE);
  }

  @Override
  public final IPjeXmlSignerBuilder xmlSignerBuilder()  {
    return xmlSignerBuilder(PJE);
  }
  
  @Override
  public final IPjeXmlSignerBuilder xmlSignerBuilder(ICertificateChooserFactory factory) {
    requireNonNull(factory, "factory is null");
    checkIfAvailable();
    return createBuilder(createChooser(factory));
  }
  
  @Override
  public final ICertificateChooser createChooser() {
    return createChooser(PJE);
  }

  private final IPjeXmlSignerBuilder createBuilder(ICertificateChooser chooser) {
    return new PjeXmlSigner.Builder(chooser, DISPOSE_ACTION);
  }
  
  @Override
  public final IPjeToken login() throws Signer4JException { 
    strategy.login(super.token);
    return this;
  }
  
  @Override
  public IPjeToken login(IPasswordCallbackHandler callback) throws Signer4JException {
    throw new LoginCanceledException("strategy for callback is not supported!");
  }
  
  @Override
  public IPjeToken login(IPasswordCollector collector) throws Signer4JException {
    throw new LoginCanceledException("strategy for password collector is not supported!");
  }

  @Override
  public IPjeToken login(char[] password) throws Signer4JException {
    throw new LoginCanceledException("strategy for literal password is not supported!");
  }
  
  @Override
  public final void logout() {
    strategy.logout(super.token);
  }

  @Override
  public void logout(boolean force) {
    if (force) {
      super.logout();
      return;
    }
    logout();
  }
}
