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


package br.jus.cnj.pje.office.core.imp.sec;

import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.AT_THIS_TIME;
import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.AWAYS;
import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.NEVER;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.core.IPjeServerAccessPersister;
import br.jus.cnj.pje.office.core.imp.PjeAccessTime;
import br.jus.cnj.pje.office.core.imp.PjePermissionDeniedException;
import br.jus.cnj.pje.office.core.imp.PjeServerAccess;
import br.jus.cnj.pje.office.core.imp.PjeServerAccessPersisters;
import br.jus.cnj.pje.office.task.IMainParams;

enum PjeSecurityAgent implements IPjeSecurityAgent {
  SAFE,
  UNSAFE(){
    @Override
    protected final boolean checkOrigin() {
      return false;
    }
  };
  
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

  @Override
  public void setDevMode() {
    this.persister = PjeServerAccessPersisters.DEVMODE.reload();
  }
  
  @Override
  public boolean isDevMode() {
    return this.persister == PjeServerAccessPersisters.DEVMODE;
  }
  
  @Override
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
    
    Optional<String> targetSchema = Strings.optional(targetUri.getScheme());
    if (!targetSchema.isPresent()) {
      whyNot.append("Parâmetro 'servidor' não define um 'schema' válido -> " + target);
      LOGGER.warn(whyNot.toString());
      return false;
    }
    
    Optional<String> targetHost = Strings.optional(targetUri.getHost());
    if (!targetHost.isPresent()) {
      whyNot.append("Parâmetro 'servidor' não define um 'host' válido -> " + target);
      LOGGER.warn(whyNot.toString());
      return false;
    }   
    
    final int targetPort = computePort(targetUri.getPort(), targetSchema.get());
    
    
    /* A sentença IF que segue considera que toda requisição ao assinador via protocolo http(s) DEVERIA
     * ser feita com POST para que ORIGIN fosse sempre conhecido e, portanto, sujeito a tratamento CSRF. Por ora esta 
     * condicional fromPostRequest deve ser testada para que os servidores que ainda não foram atualizados com a nova
     * abordagem (requisições POST) não fossem impedidos de autenticação pela ausência de ORIGIN em requisições GET 
     * (idempotência)
     */
    if (params.fromPostRequest() || checkOrigin()) {
      Optional<String> nativeOrigin = params.getOrigin();
      if (!nativeOrigin.isPresent()) {
        whyNot.append("Origem da requisição é desconhecida e foi rejeitada por segurança (CSRF prevent)");
        LOGGER.warn(whyNot.toString());
        return false;
      }
  
      final String nativeUri = nativeOrigin.get().toLowerCase();
      
      URI browserUri;
      try {
        browserUri = new URI(nativeUri);
      } catch (URISyntaxException e1) {
        whyNot.append("Header 'Origin' enviado não corresponde a uma URI válida -> " + nativeUri);
        LOGGER.warn(whyNot.toString());
        return false;
      }
      
      Optional<String> browserSchema = Strings.optional(browserUri.getScheme());
      if (!browserSchema.isPresent()) {
        whyNot.append("Header 'Origin' enviado não define um 'schema' válido -> " + nativeUri);
        LOGGER.warn(whyNot.toString());
        return false;
      }
      
      Optional<String> browserHost = Strings.optional(browserUri.getHost());
      if (!browserHost.isPresent()) {
        whyNot.append("Header 'Origin' enviado não define um 'host' válido -> " + nativeUri);
        LOGGER.warn(whyNot.toString());
        return false;
      }   
      
      final int browserPort = computePort(browserUri.getPort(), browserSchema.get());

      final String browserOrigin = (browserSchema.get() + "://" + browserHost.get()).toLowerCase() + ":" + browserPort; 

      final String targetOrigin = (targetSchema.get() + "://" + targetHost.get()).toLowerCase() + ":" + targetPort;
      
      if (!browserOrigin.equals(targetOrigin)) {
        whyNot.append("A origem da requisição é inválida e será rejeitada por segurança (CSRF prevent)");
        LOGGER.warn(whyNot.toString());
        return false;
      }
    }

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

  protected boolean checkOrigin() {
    return true;
  }

  private static int computePort(int defaultPort, String schema) {
    return defaultPort >= 0 ? defaultPort : 
      "http".equalsIgnoreCase(schema) ? 80 : 
      "https".equalsIgnoreCase(schema) ? 443 : 
      defaultPort;
  }
}
