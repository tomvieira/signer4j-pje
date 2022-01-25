package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.CONFIRM;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.ONE_TIME;
import static com.github.signer4j.imp.HttpTools.touchQuietly;
import static com.github.signer4j.imp.Strings.getQuietly;
import static com.github.signer4j.imp.Threads.async;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IWindowLockDettector;
import com.github.signer4j.IWorkstationLockListener;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.States;
import com.github.signer4j.imp.Threads;
import com.github.signer4j.imp.WindowLockDettector;
import com.github.signer4j.progress.imp.ProgressFactory;

import br.jus.cnj.pje.office.core.IPjeLifeCycleHook;
import br.jus.cnj.pje.office.core.IPjeOffice;
import br.jus.cnj.pje.office.gui.servetlist.PjeServerListAcessor;
import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;
import br.jus.cnj.pje.office.web.IPjeWebServer;
import br.jus.cnj.pje.office.web.imp.PjeWebServer;
import io.reactivex.disposables.Disposable;

public class PJeOffice implements IWorkstationLockListener, IPjeOffice {

  private static final Logger LOGGER = LoggerFactory.getLogger(PJeOffice.class);

  private IPjeWebServer webServer;

  private IPjeLifeCycleHook lifeCycle;

  private IWindowLockDettector dettector;

  private Disposable ticket;

  public PJeOffice(IPjeLifeCycleHook hook) { 
    this(WindowLockDettector.getBest(), hook);
  }

  private PJeOffice(IWindowLockDettector dettector, IPjeLifeCycleHook hook) {
    Args.requireNonNull(dettector, "dettector is null");
    Args.requireNonNull(hook, "hook is null");
    this.dettector = dettector.notifyTo(this);
    this.lifeCycle = hook;
  }
  
  private void checkIsAlive() throws IllegalStateException {
    States.requireTrue(this.lifeCycle != null, "PjeOffice was killed");
  }
  
  @Override
  public void boot() {
    checkIsAlive();
    reset();
  }
  
  @Override
  public void showCertificates() {
    checkIsAlive();
    PjeCertificateAcessor.INSTANCE.showCertificates(true, false);
  }

  @Override
  public void showAuthorizedServers() {
    checkIsAlive();
    PjeServerListAcessor.INSTANCE.show();
  }

  @Override
  public void showActivities() {
    checkIsAlive();
    ProgressFactory.DEFAULT.display();
  }

  @Override
  public void setDevMode() {
    checkIsAlive();
    PjeSecurityAgent.INSTANCE.setDevMode();
  }

  @Override
  public void setProductionMode() {
    checkIsAlive();
    PjeSecurityAgent.INSTANCE.setProductionMode();    
  }
  
  @Override
  public void setAuthStrategy(PjeAuthStrategy strategy) {
    checkIsAlive();
    PjeCertificateAcessor.INSTANCE.setAuthStrategy(strategy);
  }
  
  @Override
  public boolean isAwayStrategy() {
    checkIsAlive();
    return AWAYS == PjeCertificateAcessor.INSTANCE.getAuthStrategy();
  }

  @Override
  public boolean isOneTimeStrategy() {
    checkIsAlive();
    return ONE_TIME == PjeCertificateAcessor.INSTANCE.getAuthStrategy();
  }

  @Override
  public boolean isConfirmStrategy() {
    checkIsAlive();
    return CONFIRM == PjeCertificateAcessor.INSTANCE.getAuthStrategy();
  }
  
  private void reset() {
    stopWebServer();
    startWebServer();
  }

  protected void onWebServerStart() {
    LOGGER.info("Servidor web iniciado e pronto para receber requisições.");
    this.dettector.start();
    PjeSecurityAgent.INSTANCE.refresh();
    lifeCycle.onStartup();
  }

  protected void onWebServerStop() {
    LOGGER.info("Servidor web parado. Requisições indisponíveis");
    PjeClientMode.closeClients();
    lifeCycle.onShutdown();
  }
  
  protected void onWebServerKill() {
    LOGGER.info("Killing PjeOffice");
    this.dettector.stop();
    PjeCertificateAcessor.INSTANCE.close();
    LOGGER.info("Fechada instância certificate acessor");
    this.lifeCycle.onKill();
  }

  @Override
  public void onMachineLocked(int value) {
    checkIsAlive();
    LOGGER.info("Máquina bloqueada pelo usuário");
    stopWebServer();
    PjeCertificateAcessor.INSTANCE.close();
  }

  @Override
  public void onMachineUnlocked(int value) {
    checkIsAlive();
    LOGGER.info("Máquina desbloqueada pelo usuário");
    startWebServer();
  }

  private void startWebServer() {
    if (this.webServer == null) {
      this.webServer = new PjeWebServer(this);
      this.ticket = this.webServer.lifeCycle().subscribe(cycle -> {
        switch(cycle) {
        case STARTUP:
          onWebServerStart();
          break;
        case SHUTDOWN:
          onWebServerStop();
          break;
        case KILL:
          onWebServerKill();
        }
      });
      try {
        this.webServer.start();
      } catch (IOException e) {
        LOGGER.warn("Não foi possível iniciar o servidor web", e);
        this.lifeCycle.onFailStart(e);
      }
    }
  }
  
  private void stopWebServer() {
    stopWebServer(false);
  }

  private void stopWebServer(boolean kill) {
    if (this.webServer != null) {
      try {
        this.webServer.stop(kill);
      } catch (IOException e) {
        LOGGER.warn("Não foi possível parar o servidor web em close", e);
        this.lifeCycle.onFailShutdown(e);
      } finally {
        this.webServer = null;
        if (this.ticket != null)
          this.ticket.dispose();
        this.ticket = null;
      }
    } else if (kill) {
      onWebServerKill();
    }
  }
  
  @Override
  public void kill() {
    checkIsAlive();
    this.stopWebServer(true);
    this.lifeCycle = null;
    this.dettector = null;
    this.ticket = null;
  }
  
  @Override
  public void exit(long delay) {
    checkIsAlive();
    final Runnable action = () -> {
      Threads.sleep(delay);
      this.kill();
      LOGGER.info("Game over! Bye bye!");
      Runtime.getRuntime().halt(0);
    };
    if (Threads.isShutdownHook()) {
      LOGGER.info("Pedido de finalização via ShutdownHook");
      action.run();
      return;
    }
    async(action);
  }
  
  @Override
  public void showOfflineSigner() {
    checkIsAlive();
    final String request = 
      "{\"aplicacao\":\"Pje\"," + 
      "\"servidor\":\"localhost\"," + 
      "\"sessao\":\"localhost\"," + 
      "\"codigoSeguranca\":\"localhost\"," + 
      "\"tarefaId\":\"cnj.assinador\"," + 
      "\"tarefa\":\"{\\\"modo\\\":\\\"local\\\","
      + "\\\"padraoAssinatura\\\":\\\"NOT_ENVELOPED\\\","
      + "\\\"tipoAssinatura\\\":\\\"ATTACHED\\\","
      + "\\\"algoritmoHash\\\":\\\"MD5withRSA\\\"}\"" + 
      "}";
    
    String paramRequest = getQuietly(() -> encode(request, UTF_8.toString()), "").get();

    async(() ->  {
      try {
        webServer.setAllowLocalRequest(true);
        touchQuietly(
          "http://127.0.0.1:" + IPjeWebServer.HTTP_PORT + webServer.getTaskEndpoint() + 
          "?r=" + paramRequest + 
          "&u=" + System.currentTimeMillis()         
        );
        LOGGER.info("Finalizada requisição local");
      }finally {
        Threads.sleep(2000);        
        webServer.setAllowLocalRequest(false);
      }
    });
  }
}
