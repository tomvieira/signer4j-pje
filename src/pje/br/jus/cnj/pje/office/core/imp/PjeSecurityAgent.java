package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.AT_THIS_TIME;
import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.AWAYS;
import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.NEVER;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeMainParams;
import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;
import br.jus.cnj.pje.office.core.ISecurityAgent;
import br.jus.cnj.pje.office.core.IServerAccess;

public enum PjeSecurityAgent implements ISecurityAgent {
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
  public boolean isPermitted(IPjeMainParams params, StringBuilder whyNot) {
    
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

    Optional<String> opServer = params.getServidor();
    if (!opServer.isPresent()) {
      whyNot.append("Servidor do Pje não enviou parâmetro 'server'.");
      LOGGER.warn(whyNot.toString());
      return false;
    }
    
    final String server = opServer.get();
    final IServerAccess serverRequest = new PjeServerAccess(app, server, code);
    final Optional<IServerAccess> access = persister.hasPermission(serverRequest.getId());
    
    if (!access.isPresent()) {
      try {
        persister.checkAccessPermission(serverRequest);
        PjeAccessTime time = acessor.tryAccess(serverRequest); 
  
        if (AWAYS.equals(time) || NEVER.equals(time)) {
          persister.save(serverRequest.clone(AWAYS.equals(time)));
        }
        return AT_THIS_TIME.equals(time) || AWAYS.equals(time);
      } catch (PjePermissionDeniedException e) {
        whyNot.append("Acesso não autorizado ao servidor: '" + server + 
            "'.\nEste endereço não é reconhecido pelo CNJ.");
        LOGGER.warn(whyNot.toString(), e);
        return false;
      }
    }
    boolean ok = access.get().isAutorized();
    if (!ok) {
      whyNot.append("Acesso não autorizado ao servidor: '" + server + 
          "'.\nRevise as configurações no menu 'Servidores autorizados' do PjeOffice.");
    }
    return ok;
  }

}
