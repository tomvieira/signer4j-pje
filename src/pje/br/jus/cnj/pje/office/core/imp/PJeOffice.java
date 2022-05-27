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


package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.CONFIRM;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.ONE_TIME;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;
import static com.github.utils4j.imp.Containers.arrayList;
import static com.github.utils4j.imp.Threads.startAsync;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IWindowLockDettector;
import com.github.signer4j.IWorkstationLockListener;
import com.github.signer4j.imp.WindowLockDettector;
import com.github.signer4j.pjeoffice.shell.ShellExtensionPopup;
import com.github.utils4j.gui.imp.DefaultFileChooser;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.States;
import com.github.utils4j.imp.Threads;

import br.jus.cnj.pje.office.core.IPJeLifeCycle;
import br.jus.cnj.pje.office.core.IPjeCommandFactory;
import br.jus.cnj.pje.office.core.IPjeLifeCycleHook;
import br.jus.cnj.pje.office.core.IPjeOffice;
import br.jus.cnj.pje.office.gui.servetlist.PjeServerListAcessor;
import br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy;
import io.reactivex.disposables.Disposable;

public class PJeOffice implements IWorkstationLockListener, IPjeOffice {

  private static final Logger LOGGER = LoggerFactory.getLogger(PJeOffice.class);

  private IPJeLifeCycle commander;

  private IPjeLifeCycleHook lifeCycle;

  private IWindowLockDettector dettector;
  
  private IPjeCommandFactory factory;

  private Disposable ticket;

  private final String origin;

  public PJeOffice(IPjeLifeCycleHook lifeCycle, IPjeCommandFactory factory, String origin) { 
    this(lifeCycle, factory, origin, WindowLockDettector.getBest());
  }

  private PJeOffice(IPjeLifeCycleHook lifeCycle, IPjeCommandFactory factory, String origin, IWindowLockDettector dettector) {
    this.lifeCycle = Args.requireNonNull(lifeCycle, "hook is null");
    this.factory = Args.requireNonNull(factory, "factory is null");
    this.origin = Args.requireNonNull(origin, "origin is null");
    this.dettector =  Args.requireNonNull(dettector, "dettector is null").notifyTo(this);
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
    PjeProgressFactory.DEFAULT.display();
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
  
  @Override
  public final String getOrigin() {
    return origin;
  }
  
  private void reset() {
    stopCommander();
    startCommander();
  }

  protected void onCommanderStart() {
    LOGGER.info("Commander iniciado e pronto para receber requisições.");
    this.dettector.start();
    PjeSecurityAgent.INSTANCE.refresh();
    lifeCycle.onStartup();
  }

  protected void onCommanderStop() {
    LOGGER.info("Commander parado. Requisições indisponíveis");
    PjeClientMode.closeClients();
    lifeCycle.onShutdown();
  }
  
  protected void onCommanderKill() {
    LOGGER.info("Killing PjeOffice");
    this.dettector.stop();
    PjeCertificateAcessor.INSTANCE.close();
    LOGGER.info("Fechada instância certificate acessor");
    this.lifeCycle.onKill();
    this.lifeCycle = null;
  }

  @Override
  public void onMachineLocked(int value) {
    checkIsAlive();
    LOGGER.info("Máquina bloqueada pelo usuário");
    stopCommander();
    PjeCertificateAcessor.INSTANCE.close();
  }

  @Override
  public void onMachineUnlocked(int value) {
    checkIsAlive();
    LOGGER.info("Máquina desbloqueada pelo usuário");
    startCommander();
  }

  private void startCommander() {
    if (this.commander == null) {
      this.commander = factory.create(this);
      this.ticket = this.commander.lifeCycle().subscribe(cycle -> {
        switch(cycle) {
        case STARTUP:
          onCommanderStart();
          break;
        case SHUTDOWN:
          onCommanderStop();
          break;
        case KILL:
          onCommanderKill();
        }
      });
      try {
        this.commander.start();
      } catch (IOException e) {
        LOGGER.warn("Não foi possível iniciar o servidor web", e);
        this.lifeCycle.onFailStart(e);
      }
    }
  }
  
  private void stopCommander() {
    stopCommander(false);
  }

  private void stopCommander(boolean kill) {
    if (this.commander != null) {
      try {
        this.commander.stop(kill);
      } catch (IOException e) {
        LOGGER.warn("Não foi possível parar o servidor em close", e);
        this.lifeCycle.onFailShutdown(e);
      } finally {
        this.commander = null;
        if (this.ticket != null)
          this.ticket.dispose();
        this.ticket = null;
      }
    } else if (kill) {
      onCommanderKill();
    }
  }
  
  @Override
  public void kill() {
    checkIsAlive();
    this.stopCommander(true);
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
    invokeLater(action);
  }
  
  @Override
  public void logout() {
    checkIsAlive();
    startAsync(PjeCertificateAcessor.INSTANCE::logout);    
  }
  
  @Override
  public void selectTo() {
    checkIsAlive();
    DefaultFileChooser chooser = new DefaultFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
    chooser.setDialogTitle("Seleção de arquivos");
    if (JFileChooser.CANCEL_OPTION == chooser.showOpenDialog(null))
      return;
    File[] files = chooser.getSelectedFiles();
    if (Containers.isEmpty(files))
      return;
    ShellExtensionPopup.show(arrayList(files));
  }
}
