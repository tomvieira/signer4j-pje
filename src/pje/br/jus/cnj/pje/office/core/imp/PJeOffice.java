package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.CONFIRM;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.ONE_TIME;
import static com.github.signer4j.imp.Strings.getQuietly;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IWindowLockDettector;
import com.github.signer4j.IWorkstationLockListener;
import com.github.signer4j.imp.HttpTools;
import com.github.signer4j.imp.Threads;
import com.github.signer4j.imp.WindowLockDettector;

import br.jus.cnj.pje.office.core.IPjeLifeCycleHook;
import br.jus.cnj.pje.office.gui.PjeProgressView;
import br.jus.cnj.pje.office.gui.servetlist.PjeServerListAcessor;
import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;
import br.jus.cnj.pje.office.web.IPjeWebServer;
import br.jus.cnj.pje.office.web.imp.PjeWebServer;
import io.reactivex.disposables.Disposable;

public class PJeOffice implements IWorkstationLockListener, IPjeOffice {

  private static final Logger LOGGER = LoggerFactory.getLogger(PJeOffice.class);

  private IPjeWebServer webServer;

  private final IPjeLifeCycleHook lifeCycle;

  private final IWindowLockDettector dettector;

  private Disposable ticket;

  public PJeOffice(IPjeLifeCycleHook hook) { 
    this(WindowLockDettector.getBest(), hook);
  }

  private PJeOffice(IWindowLockDettector dettector, IPjeLifeCycleHook hook) {
    this.dettector = dettector.notifyTo(this);
    this.lifeCycle = hook;
  }
  
  @Override
  public void boot() {
    reset();
  }
  
  @Override
  public void showCertificates() {
    PjeCertificateAcessor.INSTANCE.showCertificates(true, false);
  }

  @Override
  public void showAuthorizedServers() {
    PjeServerListAcessor.INSTANCE.show();
  }

  @Override
  public void showActivities() {
    PjeProgressView.INSTANCE.display();
  }

  @Override
  public void setDevMode() {
    PjeSecurityAgent.INSTANCE.setDevMode();
  }

  @Override
  public void setProductionMode() {
    PjeSecurityAgent.INSTANCE.setProductionMode();    
  }
  
  @Override
  public void setAuthStrategy(PjeAuthStrategy strategy) {
    PjeCertificateAcessor.INSTANCE.setAuthStrategy(strategy);
  }
  
  @Override
  public boolean isAwayStrategy() {
    return AWAYS == PjeCertificateAcessor.INSTANCE.getAuthStrategy();
  }

  @Override
  public boolean isOneTimeStrategy() {
    return ONE_TIME == PjeCertificateAcessor.INSTANCE.getAuthStrategy();
  }

  @Override
  public boolean isConfirmStrategy() {
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
    if (this.ticket != null)
      this.ticket.dispose();
    this.ticket = null;
    this.webServer = null;
  }
  
  protected void onWebServerKill() {
    LOGGER.info("Killing PjeOffice");
    this.dettector.stop();
    PjeCertificateAcessor.INSTANCE.close();
    this.lifeCycle.onKill();
  }

  @Override
  public void onMachineLocked(int value) {
    LOGGER.info("Máquina bloqueada pelo usuário");
    stopWebServer();
    PjeCertificateAcessor.INSTANCE.close();
  }

  @Override
  public void onMachineUnlocked(int value) {
    LOGGER.info("Máquina desbloqueada pelo usuário");
    startWebServer();
  }

  private void startWebServer() {
    if (this.webServer == null) {
      this.webServer = new PjeWebServer(PjeCertificateAcessor.INSTANCE, PjeSecurityAgent.INSTANCE);
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
      }
    }
  }
  
  @Override
  public void exit() {
    Threads.async(() -> {
      this.stopWebServer(true);
      System.exit(0);
    });
  }
  
  @Override
  public void showOfflineSigner() {
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

    Threads.async(() ->  {
      try {
        webServer.setAllowLocalRequest(true);
        HttpTools.sendGetRequestAndDisconnect(
          "http://127.0.0.1:8800" + webServer.getTaskEndpoint() + 
          "?r=" + paramRequest + 
          "&u=" + System.currentTimeMillis(),
          "PjeOffice (Offline Signer)"
        );
        LOGGER.info("Finalizada requisição local");
      }finally {
        webServer.setAllowLocalRequest(false);
      }
    });
  }
}
