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


package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.gui.certlist.PjeCertificateListAcessor.SUPPORTED_CERTIFICATE;
import static com.github.signer4j.IFilePath.toPaths;
import static com.github.signer4j.imp.DeviceCertificateEntry.toEntries;
import static com.github.signer4j.imp.Signer4JInvoker.SIGNER4J;
import static com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException.lambda;
import static com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException.of;
import static com.github.utils4j.gui.imp.SwingTools.invokeAndWait;
import static com.github.utils4j.gui.imp.SwingTools.isTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.ICertificateListUI.IChoice;
import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.IDriverVisitor;
import com.github.signer4j.IFilePath;
import com.github.signer4j.TokenType;
import com.github.signer4j.gui.CertificateListUI;
import com.github.signer4j.gui.alert.ExpiredPasswordAlert;
import com.github.signer4j.gui.alert.NoTokenPresentAlert;
import com.github.signer4j.gui.alert.TokenLockedAlert;
import com.github.signer4j.gui.utils.InvalidPinAlert;
import com.github.signer4j.imp.AbstractStrategy;
import com.github.signer4j.imp.DeviceCertificateEntry;
import com.github.signer4j.imp.DeviceManager;
import com.github.signer4j.imp.NotDuplicatedStrategy;
import com.github.signer4j.imp.exception.ExpiredCredentialException;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.TokenLockedException;

import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;
import br.jus.cnj.pje.office.signer4j.imp.PjeToken;

public enum PjeCertificateAcessor implements IPjeTokenAccess {
  
  INSTANCE;
  
  private class FilePathStrategy extends AbstractStrategy {
    @Override
    public void lookup(IDriverVisitor visitor) {
      a3Libraries.forEach(fp -> createAndVisit(Paths.get(fp.getPath()), visitor));
    }
  }
  
  private volatile IPjeToken token;

  private boolean autoForce = true;

  private IPjeAuthStrategy strategy;

  private final IDeviceManager devManager;

  private List<IFilePath> a1Files = new ArrayList<>();
  
  private List<IFilePath> a3Libraries = new ArrayList<>();

  private PjeCertificateAcessor() {
    PjeConfig.loadA1Paths(a1Files::add);
    PjeConfig.loadA3Paths(a3Libraries::add);
    this.strategy = PjeAuthStrategy.getDefault();
    this.devManager = new DeviceManager(new NotDuplicatedStrategy(new FilePathStrategy())).install(toPaths(a1Files));
    //this.devManager = new MSCAPIDevManager();
  }
  
  private IPjeToken toToken(IDevice device) {
    return new PjeToken(device.getSlot().getToken(), strategy);
  }
  
  private void onNewDevices(List<IFilePath> a1List, List<IFilePath> a3List) {
    this.a3Libraries = a3List;
    this.a1Files = a1List;
    this.autoForce = true;
    this.close();
    this.devManager.install(toPaths(a1List));
  }

  private Optional<IPjeToken> getToken(boolean force, boolean autoSelect) {
    if (!force && this.token != null)
      return Optional.of(this.token);
    force |= this.token == null;
    final Optional<ICertificateEntry> selected = showCertificates(force, autoSelect);
    if (selected.isPresent()) {
      DeviceCertificateEntry e = (DeviceCertificateEntry)selected.get();
      Optional<IDevice> device = e.getNative();
      if (device.isPresent()) {
        return Optional.of(this.token = toToken(device.get()));
      }
    }
    return Optional.empty();
  }

  public IPjeAuthStrategy getAuthStrategy() {
    return this.strategy;
  }

  public void setAuthStrategy(IPjeAuthStrategy strategy) {
    if (strategy != null) {
      PjeConfig.save(this.strategy = strategy);
      this.logout();
      this.close();      
    }
  }
  
  @Override
  public void logout() {
    if (token != null) {
      token.logout(true);
      token = null;
    }
  }
  
  @Override
  public final void close() { 
    try {
      this.devManager.close();
    } finally {
      this.token = null;
    }
  }

  @Override
  public Optional<ICertificateEntry> showCertificates(boolean force, boolean autoSelect) {
    IChoice choice;
    do {
      List<IDevice> devices = this.devManager.getDevices(this.autoForce || force); 
      choice = CertificateListUI.display(
        toEntries(devices, SUPPORTED_CERTIFICATE), 
        autoSelect, 
        this::onNewDevices
      );
    }while(choice == IChoice.NEED_RELOAD);
    this.autoForce = false;
    this.close();
    return choice.get();
  }
  
  @Override
  public IPjeToken get() {
    boolean force = false, autoSelect = true;
    int times = 0;
    do {
      IPjeToken pjeToken = getToken(force, autoSelect).orElseThrow(lambda(LoginCanceledException::new));
      try {
        return SIGNER4J.invoke(pjeToken::login);
      } catch (LoginCanceledException e) {
        throw of(e);
      } catch (NoTokenPresentException e) {
        if (!isTrue(NoTokenPresentAlert::display))
          throw of(e);
        this.close();
        force = true;
        autoSelect = false;
      } catch (TokenLockedException e) {
        invokeAndWait(TokenLockedAlert::display);
        throw of(e);
      } catch (ExpiredCredentialException e) {
        invokeAndWait(ExpiredPasswordAlert::display);
        throw of(e);
      } catch (InvalidPinException e) {
        if (TokenType.A3.equals(pjeToken.getType()))
          ++times;
        final int t = times;
        if (isTrue(() -> InvalidPinAlert.display(t, PjeConfig.getIcon())))
          continue;
        throw of(e);
      } catch (Signer4JException e) {
        throw of(e);
      }
    }while(true);
  }
}
