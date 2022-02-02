package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Constants.DEFAULT_CHARSET;
import static com.github.signer4j.imp.Strings.trim;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Base64;
import com.github.signer4j.imp.Certificates;
import com.github.signer4j.imp.Ciphers;

import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.IPjeServerAccessPermissionChecker;

enum PjePermissionChecker implements IPjeServerAccessPermissionChecker {
  DEVMODE() {
    @Override
    public void checkAccessPermission(IPjeServerAccess token) throws PjePermissionDeniedException  {
      //nothing to do allow all
    }    
  },
  
  DENY_ALL() {
    @Override
    public void checkAccessPermission(IPjeServerAccess access) throws PjePermissionDeniedException {
      throw new PjePermissionDeniedException("Permissão negada (deny all implementation)");
    }  
  },
  
  PRODUCTION(){
    
    @Override
    public void checkAccessPermission(IPjeServerAccess access)  throws PjePermissionDeniedException {
      Args.requireText(access, "access is null");
      
      final byte[] cryptedBytes = Base64.base64Decode(access.getCode());
      
      final byte[] decriptedBytes;
      try {
        decriptedBytes = Ciphers.decryptWithRsa(cryptedBytes, KEY);
      } catch (Exception e) {
        throw new PjePermissionDeniedException("Não foi possível descriptografar código de segurança. Acesso negado!", e);
      }
      
      String cleanCode = trim(new String(decriptedBytes, DEFAULT_CHARSET));
      
      final int dots = cleanCode.indexOf(':');
      if (dots < 0 || dots == cleanCode.length() - 1) {
        throw new PjePermissionDeniedException("Parâmetro em formato inválido: " + cleanCode + ". Contactar CNJ ");
      }
      
      final String appCode = trim(cleanCode.substring(0, dots));
      if (!access.getApp().equals(appCode)) {
        throw new PjePermissionDeniedException(String.format("Código de segurança inválido. "
            + "Parâmetro 'aplicacao' não confere: '%s' e '%s'. Acesso negado!", access.getApp(), appCode));
      }
      
      String urls = trim(cleanCode.substring(dots + 1));
      do {
        String url;
        int comma = urls.indexOf(',');
        if (comma < 0) {
          url = urls;
          urls = ""; //finishing!
        }else {
          url = trim(urls.substring(0, comma));
          urls = trim(urls.substring(comma + 1)); //advance!
        }
        if (access.getServer().equalsIgnoreCase(url)) {
          return;
        }
      }while(!urls.isEmpty());
      
      throw new PjeServerAccessPermissionException("Acesso ao servidor: " + access.getServer() + " não autorizado");
    }
  };
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjePermissionChecker.class);
  
  protected static PublicKey KEY;
  
  static {
    try(InputStream is = IPjeSecurityAgent.class.getResourceAsStream("/PJeOffice.cer")) {
      KEY = Certificates.create(is).getPublicKey();
    } catch (CertificateException | IOException e) {
      LOGGER.error("Unabled to read public key from 'PjeOffice.cer'", e);
    }
  }
}
