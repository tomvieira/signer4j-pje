package br.jus.cnj.pje.office.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ICertificateChooserFactory;
import com.github.signer4j.IPKCS7SignerBuilder;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.IToken;
import com.github.signer4j.exception.NotAuthenticatedException;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.TokenWrapper;
import com.github.signer4j.imp.exception.Signer4JException;

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
  public final IToken login() throws Signer4JException { 
    strategy.login(super.token);
    return this;
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
