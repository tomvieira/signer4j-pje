package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.IFilePath.toPaths;
import static br.jus.cnj.pje.office.core.imp.PJeConfigPersister.CONF;
import static br.jus.cnj.pje.office.gui.certlist.PjeCertificateList.display;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static com.github.signer4j.imp.KeyStoreInvokeHandler.INVOKER;
import static com.github.signer4j.imp.LookupStrategy.notDuplicated;
import static com.github.signer4j.imp.SwingTools.invokeAndWait;
import static com.github.signer4j.imp.SwingTools.isTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateList.ICertificateEntry;
import com.github.signer4j.IDevice;
import com.github.signer4j.IDeviceManager;
import com.github.signer4j.TokenType;
import com.github.signer4j.gui.utils.InvalidPinAlert;
import com.github.signer4j.imp.AbstractStrategy;
import com.github.signer4j.imp.DefaultCertificateEntry;
import com.github.signer4j.imp.DeviceManager;
import com.github.signer4j.imp.EnvironmentStrategy;
import com.github.signer4j.imp.IDriverVisitor;
import com.github.signer4j.imp.exception.ExpiredCredentialException;
import com.github.signer4j.imp.exception.InvalidPinException;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.LoginCanceledException;
import com.github.signer4j.imp.exception.NoTokenPresentException;
import com.github.signer4j.imp.exception.RuntimeKeyStoreException;
import com.github.signer4j.imp.exception.TokenLockedException;

import br.jus.cnj.pje.office.core.IFilePath;
import br.jus.cnj.pje.office.core.IPjeCertificateAcessor;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.gui.Images;
import br.jus.cnj.pje.office.gui.alert.ExpiredPasswordAlert;
import br.jus.cnj.pje.office.gui.alert.NoTokenPresentAlert;
import br.jus.cnj.pje.office.gui.alert.TokenLockedAlert;
import br.jus.cnj.pje.office.gui.certlist.PjeCertificateListAcessor;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;
import br.jus.cnj.pje.office.signer4j.imp.PjeToken;

public enum PjeCertificateAcessor implements IPjeCertificateAcessor, IPjeTokenAccess {
  
  INSTANCE;
  
  private static class CertifiateEntry extends DefaultCertificateEntry {
    CertifiateEntry(IDevice device, ICertificate certificate) {
      super(device, certificate);
    }
    IDevice device() {
      return super.device;
    }
  }
  
  private static List<ICertificateEntry> toEntries(List<IDevice> devices) {
    final List<ICertificateEntry> entries = new ArrayList<>();
    devices.forEach(d -> {
      d.getCertificates()
        .stream()
        .filter(PjeCertificateListAcessor.SUPPORTED_CERTIFICATE)
        .forEach(c -> entries.add(new CertifiateEntry(d, c)));
    });
    return entries;
  }
  
  private volatile IPjeToken token;
  
  private IPjeAuthStrategy strategy;

  private final IDeviceManager devManager;
  
  private List<IFilePath> a3Libraries = new ArrayList<>();

  private List<IFilePath> a1Files = new ArrayList<>();

  private PjeCertificateAcessor() {
    this.strategy = PjeAuthStrategy.valueOf(CONF.authStrategy().orElse(AWAYS.name()).toUpperCase());
    CONF.loadA3Paths(a3Libraries::add);
    CONF.loadA1Paths(a1Files::add);
    this.devManager = new DeviceManager(notDuplicated()
      .more(new EnvironmentStrategy())
      .more(new FilePathStrategy())
    );
    this.devManager.install(toPaths(a1Files));
  }
  
  private class FilePathStrategy extends AbstractStrategy {
    @Override
    public void lookup(IDriverVisitor visitor) {
      a3Libraries.forEach(fp -> createAndVisit(Paths.get(fp.getPath()), visitor));
    }
  }
  
  private boolean autoForce = true;
 
  @Override
  public synchronized Optional<ICertificateEntry> showCertificates(boolean force, boolean autoSelect) {
    Optional<ICertificateEntry> selected;
    do {
      List<IDevice> devices = this.devManager.getDevices(autoForce || force); 
      selected = display(
        toEntries(devices), 
        this::onNewDevices, 
        autoSelect
      );
    }while(selected == null);
    this.autoForce = false;
    this.close();
    return selected;
  }
  
  private void onNewDevices(List<IFilePath> a1List, List<IFilePath> a3List) {
    this.a3Libraries = a3List;
    this.a1Files = a1List;
    this.autoForce = true;
    this.close();
    this.devManager.install(toPaths(a1List));
  }

  public synchronized void setAuthStrategy(IPjeAuthStrategy strategy) {
    if (strategy != null) {
      this.strategy = strategy;
      this.close();
      CONF.save(strategy);
    }
  } 
  
  public synchronized IPjeAuthStrategy getAuthStrategy() {
    return this.strategy;
  }

  private IPjeToken toToken(IDevice device) {
    return new PjeToken(device.getSlot().getToken(), strategy);
  }
  
  @Override
  public void close() {
    try {
      if (token != null) {
        token.logout(true);
        token = null;
      }
    } finally {
      devManager.close();
    }
  }
  
  private Optional<IPjeToken> getToken(boolean force, boolean autoSelect) {
    if (!force && token != null)
      return Optional.of(token);
    force |= token == null;
    final Optional<ICertificateEntry> selected = showCertificates(force, autoSelect);
    if (selected.isPresent()) {
      CertifiateEntry e = (CertifiateEntry)selected.get();
      return Optional.of(token = toToken(e.device()));
    }
    return Optional.empty();
  }
  
  @Override
  public synchronized  IPjeToken get() {
    boolean force = false, autoSelect = true;
    int times = 0;
    do {
      Optional<IPjeToken> opToken = getToken(force, autoSelect);
      if (!opToken.isPresent()) {
        throw new RuntimeKeyStoreException(new LoginCanceledException());
      }
      IPjeToken pjeToken = (IPjeToken)opToken.get();
      try {
        return INVOKER.invoke(() -> (IPjeToken)pjeToken.login());
      } catch (LoginCanceledException e) {
        throw new RuntimeKeyStoreException(e);
      } catch (NoTokenPresentException e) {
        if (!isTrue(NoTokenPresentAlert::display))
          throw new RuntimeKeyStoreException(e);
        this.close();
        force = true;
        autoSelect = false;
      } catch (TokenLockedException e) {
        invokeAndWait(TokenLockedAlert::display);
        throw new RuntimeKeyStoreException(e);
      } catch (ExpiredCredentialException e) {
        invokeAndWait(ExpiredPasswordAlert::display);
        throw new RuntimeKeyStoreException(e);
      } catch (InvalidPinException e) {
        if (TokenType.A3.equals(pjeToken.getType()))
          ++times;
        final int t = times;
        if (isTrue(() -> InvalidPinAlert.display(t, Images.PJE_ICON.asImage())))
          continue;
        throw new RuntimeKeyStoreException(e);
      } catch (KeyStoreAccessException e) {
        throw new RuntimeKeyStoreException(e);
      }
    }while(true);
  }
}