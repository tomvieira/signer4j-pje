package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.AT_THIS_TIME;
import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.AWAYS;
import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.NEVER;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;
import br.jus.cnj.pje.office.task.IMainParams;

public enum PjeSecurityAgent implements IPjeSecurityAgent {
  INSTANCE;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeSecurityAgent.class);

  private final IPjePermissionAccessor acessor;

  private volatile IPjeServerAccessPersister persister;
  
  private PjeSecurityAgent() {
    this(PjePermissionAcessor.PRODUCTION);
  }

  private PjeSecurityAgent(IPjePermissionAccessor acessor) {
    this(acessor, PjeServerAccessPersisters.PRODUCTION);
  }

  private PjeSecurityAgent(IPjePermissionAccessor acessor, IPjeServerAccessPersister persister) {
    this.acessor = Args.requireNonNull(acessor, "acessor is null");
    this.persister = Args.requireNonNull(persister, "persister is null");
  }

  public void setDevMode() {
    this.persister = PjeServerAccessPersisters.DEVMODE.reload();
  }
  
  public void setProductionMode() {
    this.persister = PjeServerAccessPersisters.PRODUCTION.reload();
  }

  @Override
  public void refresh() {
    this.persister.reload();
  }
  
  @Override
  public boolean isPermitted(IMainParams params, StringBuilder whyNot) {

    Optional<String> opCode = params.getCodigoSeguranca();
    if (!opCode.isPresent()) {
      whyNot.append("Servidor do Pje não enviou parâmetro 'codigoSeguranca'.");
      LOGGER.warn(whyNot.toString());
      return false;
    }
    final String code = opCode.get();

    Optional<String> opApp = params.getAplicacao();
    if (!opApp.isPresent()) {
      whyNot.append("Servidor do Pje não enviou parâmetro 'aplicacao'.");
      LOGGER.warn(whyNot.toString());
      return false;
    }

    final String app = opApp.get();

    Optional<String> opTarget = params.getServidor();
    if (!opTarget.isPresent()) {
      whyNot.append("Servidor do Pje não enviou parâmetro 'servidor'.");
      LOGGER.warn(whyNot.toString());
      return false;
    }        
    
    final String target = opTarget.get();
    
    URI targetUri;
    try {
      targetUri = new URI(target);
    } catch (URISyntaxException e1) {
      whyNot.append("Parâmetro 'servidor' não corresponde a uma URI válida -> " + target);
      LOGGER.warn(whyNot.toString());
      return false;
    }
    
    Optional<String> targetSchema = Optional.ofNullable(targetUri.getScheme());
    if (!targetSchema.isPresent()) {
      whyNot.append("Parâmetro 'servidor' não define um 'schema' válido -> " + target);
      LOGGER.warn(whyNot.toString());
      return false;
    }
    
    Optional<String> targetHost = Optional.ofNullable(targetUri.getHost());
    if (!targetHost.isPresent()) {
      whyNot.append("Parâmetro 'servidor' não define um 'host' válido -> " + target);
      LOGGER.warn(whyNot.toString());
      return false;
    }
    
    Optional<String> nativeOrigin = params.getOrigin();
    if (!nativeOrigin.isPresent()) {
      whyNot.append("Origem da requisição é desconhecida e será rejeitada por segurança (CSRF prevent)");
      LOGGER.warn(whyNot.toString());
      return false;
    }
    
    final String targetOrigin = targetSchema.get() + "://" + targetHost.get();
    
/*
    //Descomente este trecho de código quando o navegador enviar o 'Origin' para o assinador via post
     
    if (!nativeOrigin.get().equals(targetOrigin)) {
      whyNot.append("A origem da requisição é inválida e será rejeitada por segurança (CSRF prevent)");
      LOGGER.warn(whyNot.toString());
      return false;
    }
*/    
    final IPjeServerAccess serverRequest = new PjeServerAccess(app, target, code);
    final Optional<IPjeServerAccess> access = persister.hasPermission(serverRequest.getId());
    
    if (!access.isPresent()) {
      try {
        persister.checkAccessPermission(serverRequest);
        PjeAccessTime time = acessor.tryAccess(serverRequest); 
  
        if (AWAYS.equals(time) || NEVER.equals(time)) {
          persister.save(serverRequest.clone(AWAYS.equals(time)));
        }
        return AT_THIS_TIME.equals(time) || AWAYS.equals(time);
      } catch (PjePermissionDeniedException e) {
        whyNot.append("Acesso não autorizado ao servidor: '" + target + 
            "'.\nEste endereço não é reconhecido pelo CNJ.");
        LOGGER.warn(whyNot.toString(), e);
        return false;
      }
    }
    boolean ok = access.get().isAutorized();
    if (!ok) {
      whyNot.append("Acesso não autorizado ao servidor: '" + target + 
          "'.\nRevise as configurações no menu 'Servidores autorizados' do PjeOffice.");
    }
    return ok;
  }
}
