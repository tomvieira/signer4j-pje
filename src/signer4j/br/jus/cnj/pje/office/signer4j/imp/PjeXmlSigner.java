package br.jus.cnj.pje.office.signer4j.imp;

import static com.github.utils4j.IConstants.DEFAULT_CHARSET;
import static com.github.utils4j.imp.Strings.empty;
import static com.github.utils4j.imp.Strings.trim;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IChoice;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.SecurityObject;
import com.github.signer4j.imp.SignException;
import com.github.signer4j.imp.SignedData;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.OpenByteArrayOutputStream;
import com.github.utils4j.imp.ProviderInstaller;
import com.github.utils4j.imp.Strings;
import com.github.utils4j.imp.Throwables;

import br.jus.cnj.pje.office.signer4j.IPjeXmlSigner;
import br.jus.cnj.pje.office.signer4j.IPjeXmlSignerBuilder;

class PjeXmlSigner extends SecurityObject implements IPjeXmlSigner {

  private XMLSignatureFactory xmlSignatureFactory;
  private DocumentBuilder documentBuilder;
  private KeyInfoFactory keyInfoFactory;
  private Transformer outputTransformer;
  private SignedInfo signedInfo;

  private PjeXmlSigner(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }

  @Override
  public ISignedData process(byte[] content, int offset, int length) throws Signer4JException {
    Args.requireNonEmpty(content, "content is null");
    Args.requireZeroPositive(offset, "offset is negative");
    Args.requireZeroPositive(length, "length is negative");
    try(InputStream is = new ByteArrayInputStream(content, offset, length)) {
      return process(is);
    } catch (IOException e) {
      throw new Signer4JException(e);
    }
  }

  @Override
  public ISignedData process(File content) throws Signer4JException, IOException {
    Args.requireNonNull(content, "content is null");
    try(InputStream is = new BufferedInputStream(new FileInputStream(content), 32*1024)) {
      return process(is);
    }
  }

  @Override
  public ISignedData process(InputStream input) throws Signer4JException {
    Args.requireNonNull(input, "input is null");
    return invoke(() -> {
      IChoice choice = choose();
      return SignedData.from(
        sign(
          input, 
          createKeyInfo(choice.getCertificate()), 
          choice.getPrivateKey()
        ), 
        choice
      );
    });
  }

  private KeyInfo createKeyInfo(Certificate certificate) {
    return keyInfoFactory.newKeyInfo(singletonList(keyInfoFactory.newX509Data(asList(certificate))));
  }

  private byte[] sign(InputStream input, KeyInfo keyInfo, PrivateKey privateKey) throws Exception {
    final Document document = documentBuilder.parse(input);
    final Element elSignature = document.getDocumentElement();
    final DOMSignContext dsc = new DOMSignContext(privateKey, elSignature);
    final XMLSignature signature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);
    signature.sign(dsc);
    return toByteArray(document);
  }

  private static final String[] SEARCH_CLEANER = new String[] {"\n", "\r", " standalone=\"no\""};
  private static final String[] REPLACE_CLEANER = new String[] {empty(), empty(), empty()};

  private byte[] toByteArray(Document document) throws Exception {
    try(OpenByteArrayOutputStream out = new OpenByteArrayOutputStream()){
      this.outputTransformer.transform(new DOMSource(document), new StreamResult(out));
      final String content = Strings.replaceEach(
          trim(out.asString(DEFAULT_CHARSET)), 
          SEARCH_CLEANER, 
          REPLACE_CLEANER
          );
      if (content.isEmpty()) {
        throw new SignException("Arquivo final não pôde ser assinado (vazio)");
      }
      return content.getBytes(IConstants.DEFAULT_CHARSET);
    }
  }

  static class Builder implements IPjeXmlSignerBuilder {

    private final ICertificateChooser chooser;

    private final Runnable dispose;

    private String hashPath = "http://www.w3.org/2001/04/xmlenc#sha256";

    private String asymetricPath = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    private String c14nTransformPath = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    private String envelopedTransformPath = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    public Builder(ICertificateChooser chooser, Runnable dispose) {
      this.chooser = Args.requireNonNull(chooser, "chooser is null");
      this.dispose = Args.requireNonNull(dispose, "dispose is null");
    }

    @Override
    public Builder usingHashPath(String hashPath) {
      this.hashPath = Strings.needText(hashPath,  this.hashPath);
      return this;
    }

    @Override
    public Builder usingAsymetricPath(String asymetricPath) {
      this.asymetricPath = Strings.needText(asymetricPath, this.asymetricPath);
      return this;
    }

    @Override
    public Builder usingC14nTransformPath(String c14nTransformPath) {
      this.c14nTransformPath = Strings.needText(c14nTransformPath, this.c14nTransformPath);
      return this;
    }

    @Override
    public Builder usingEnvelopedTransform(String envelopedTransform) {
      this.envelopedTransformPath = Strings.needText(envelopedTransform, this.envelopedTransformPath);
      return this;
    }

    @Override
    public final IPjeXmlSigner build() {
      PjeXmlSigner signer = new PjeXmlSigner(chooser, dispose);
      Throwables.tryRuntime(() -> {
        final XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", ProviderInstaller.JSR105.install());
        final KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();

        Transform envelopedTransform = xmlSignatureFactory.newTransform(
            envelopedTransformPath, 
            (TransformParameterSpec)null
            );
        Transform c14nTransform = xmlSignatureFactory.newTransform(
            c14nTransformPath, 
            (TransformParameterSpec)null
            );
        Reference ref = xmlSignatureFactory.newReference(
            "", 
            xmlSignatureFactory.newDigestMethod(hashPath, null), 
            Arrays.asList(envelopedTransform, c14nTransform), 
            null, 
            null
            );
        final SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(
            xmlSignatureFactory.newCanonicalizationMethod(c14nTransformPath, (C14NMethodParameterSpec)null), 
            xmlSignatureFactory.newSignatureMethod(asymetricPath, null), 
            Collections.singletonList(ref)
            );
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        signer.xmlSignatureFactory = xmlSignatureFactory;
        signer.keyInfoFactory = keyInfoFactory;
        signer.documentBuilder = documentBuilderFactory.newDocumentBuilder();
        signer.outputTransformer = TransformerFactory.newInstance().newTransformer();
        signer.signedInfo = signedInfo;
        }, 
        "Não foi possível instanciar PjeXmlSigner"
      );
      return signer;
    }
  }
}
