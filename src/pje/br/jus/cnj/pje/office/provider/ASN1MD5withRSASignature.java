package br.jus.cnj.pje.office.provider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;

//Meu deus! Turbo ultra mega super gambiarra supreme! Aff! Esse pacote inteiro tem que desaparecer daqui!
@Deprecated
public class ASN1MD5withRSASignature extends Signature implements Cloneable{

  private static final String PROVIDER_SUN_MSCAPI = "SunMSCAPI";
  private static final String PROVIDER_BC = "BC";
  private String provider = "";
  private Signature delegateSignature;
  
  public ASN1MD5withRSASignature() throws NoSuchAlgorithmException{
    super("ASN1MD5withRSA");
  }

  public void engineInitVerify(PublicKey publicKey) throws InvalidKeyException{
    throw new IllegalArgumentException("Para verificar assinatura, usar o MD5withRSA.");
  }

  public void engineInitSign(PrivateKey privateKey) throws InvalidKeyException{
    try {
      if (privateKey.getClass().getCanonicalName().contains("sun.security.mscapi")) {
        
        Signature signatureMSCAPI = Signature.getInstance("MD5withRSA", PROVIDER_SUN_MSCAPI);
        MessageDigest digestNullMd5 = MessageDigest.getInstance("nullMD5");
        
        digestNullMd5.reset();
        
        Field f = Class.forName("java.security.Signature$Delegate").getDeclaredField("sigSpi");
        f.setAccessible(true);
            
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        SignatureSpi signatureSpi = (SignatureSpi) f.get(signatureMSCAPI);
        
        Field fMessageDigest = getMessageDigestMSCAPI();
        fMessageDigest.setAccessible(true);
            
        Field modifiersFieldMessageDigest = Field.class.getDeclaredField("modifiers");
        modifiersFieldMessageDigest.setAccessible(true);
        modifiersFieldMessageDigest.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        fMessageDigest.set(signatureSpi, digestNullMd5);
        
        delegateSignature = signatureMSCAPI;
        provider = PROVIDER_SUN_MSCAPI;
      } else {
        delegateSignature = Signature.getInstance("nonewithRSA");
        provider = PROVIDER_BC;
      }
      delegateSignature.initSign(privateKey);
    } catch (Exception e) {
      throw new IllegalArgumentException("Não foi possível assinar, utilizar fallback para garantir assinatura.");
    }
  }

  private Field getMessageDigestMSCAPI() throws NoSuchFieldException, SecurityException, ClassNotFoundException {
      try {
          //log.info("Tentando recuperar MessageDigest do provider MSCAPI...");
            return Class.forName("sun.security.mscapi.CSignature").getDeclaredField("messageDigest");
        } catch (ClassNotFoundException e) {
            //log.info("Verso do Java  anterior a 8u251. Tentando recuperar classe legada...");
        }
      return Class.forName("sun.security.mscapi.RSASignature").getDeclaredField("messageDigest");
  }

  public void engineUpdate(byte b) throws SignatureException{
    try{
      if (PROVIDER_BC.equals(provider)) {
        throw new IllegalArgumentException("Operação não disponível, utilizar fallback para garantir assinatura.");
      } else {
        delegateSignature.update(b);
      }
    } catch (NullPointerException npe){
      throw new SignatureException("No SHA digest found");
    }
  }

  public void engineUpdate(byte b[], int offset, int length) throws SignatureException{
    try{
      if (PROVIDER_BC.equals(provider)) {
        // gerar digest
        DigestInfo dInfo = new DigestInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.md5, DERNull.INSTANCE), 
          Arrays.copyOfRange(b, offset, length) //this is correct way to use range instead of only use 'b' reference
        );
        try{
          delegateSignature.update(dInfo.getEncoded(ASN1Encoding.DER));
        } catch (IOException e){
          throw new IllegalArgumentException("Erro ao gerar ASN.1 do provider 'BC', utilizar fallback para garantir assinatura.");
        }
      } else {
        delegateSignature.update(b, offset, length);
      }
    } catch (NullPointerException npe){
      throw new SignatureException("No SHA digest found");
    }
  }

  public byte[] engineSign() throws SignatureException{
    try{
      return delegateSignature.sign();
    } catch (NullPointerException npe){
      throw new SignatureException("No SHA digest found");
    }
  }

  public boolean engineVerify(byte[] sigBytes) throws SignatureException{
    throw new IllegalArgumentException("Para verificar assinatura, usar o MD5withRSA.");
  }

  public void engineSetParameter(String param, Object value){
    throw new InvalidParameterException("No parameters");
  }

  public void engineSetParameter(AlgorithmParameterSpec aps){
    throw new InvalidParameterException("No parameters");
  }

  public Object engineGetParameter(String param){
    throw new InvalidParameterException("No parameters");
  }

  public void engineReset(){
  }

}
