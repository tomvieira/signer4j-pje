package br.jus.cnj.pje.office.gui.certlist;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.gui.CertificateListUI;
import com.github.signer4j.imp.AbstractCertificateChooser;
import com.github.signer4j.imp.Choice;
import com.github.signer4j.imp.IChoice;
import com.github.signer4j.imp.exception.Signer4JException;

public class PjeCertificateListAcessor extends AbstractCertificateChooser {
  
  public static final Predicate<ICertificate> SUPPORTED_CERTIFICATE =  c -> c.getKeyUsage().isDigitalSignature() && (c.hasCertificatePF() || c.hasCertificatePJ());

  public PjeCertificateListAcessor(IKeyStoreAccess keyStore, ICertificates certificates) {
    super(keyStore, certificates);
  }
  
  @Override
  protected Predicate<ICertificate> getPredicate() {
    return SUPPORTED_CERTIFICATE;
  }
 
  @Override
  protected IChoice doChoose(List<CertificateEntry> options) throws Signer4JException {
    if (options.size() == 1)
      return toChoice(options.get(0));
    @SuppressWarnings({ "unchecked", "rawtypes" })
    Optional<ICertificateEntry> ce = CertificateListUI.display((List)options);
    if (!ce.isPresent()) {
      return Choice.CANCEL;
    }
    return toChoice((CertificateEntry)ce.get());
  }
}
