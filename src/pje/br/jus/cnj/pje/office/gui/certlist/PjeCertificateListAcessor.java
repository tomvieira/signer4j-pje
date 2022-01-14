package br.jus.cnj.pje.office.gui.certlist;

import java.util.List;
import java.util.function.Predicate;

import com.github.signer4j.ICertificate;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IChoice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.imp.DefaultChooser;
import com.github.signer4j.imp.exception.Signer4JException;

public class PjeCertificateListAcessor extends DefaultChooser {
  
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
    return super.doChoose(options);
  }
}
