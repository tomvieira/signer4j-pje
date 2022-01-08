package br.jus.cnj.pje.office.core;

import java.util.Optional;

import com.github.signer4j.ICertificateListUI.ICertificateEntry;

public interface IPjeCertificateAcessor {
  Optional<ICertificateEntry> showCertificates(boolean force, boolean autoSelect);
  
  void close();
}
