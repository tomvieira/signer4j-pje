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
    if (options.size() == 1) {
      CertificateEntry c = options.get(0);
      if (!c.isExpired() /* && c.isRemembered()*/) {
        return toChoice(c);
      }
    }
    return super.doChoose(options);
  }
}
