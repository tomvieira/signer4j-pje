package br.jus.cnj.pje.office.signer4j;

public interface IPjeXmlSignerBuilder {
  
  IPjeXmlSigner build();

  IPjeXmlSignerBuilder usingHashPath(String hashPath);

  IPjeXmlSignerBuilder usingAsymetricPath(String asymetricPath);

  IPjeXmlSignerBuilder usingC14nTransformPath(String transformPath);
  
  IPjeXmlSignerBuilder usingEnvelopedTransform(String envelopedTransform);

}
