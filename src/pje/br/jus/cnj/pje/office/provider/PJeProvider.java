package br.jus.cnj.pje.office.provider;

import java.security.Provider;

//Gambiarra braba! Essa classe tem que desaparecer!
@Deprecated
public class PJeProvider extends Provider {
  
  private static final long serialVersionUID = 1L;

  public PJeProvider() {
    super("PJE", 1.0, "PJe Security Provider v1.0");
    this.put("Signature.ASN1MD5withRSA", "br.jus.cnj.pje.office.provider.ASN1MD5withRSASignature");
    this.put("MessageDigest.nullMD5", "br.jus.cnj.pje.office.provider.NullMD5MessageDigest");
  }
}
