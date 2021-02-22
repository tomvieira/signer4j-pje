
package br.jus.cnj.pje.office.imp;  

import static br.jus.cnj.pje.office.imp.PjeOfficeFrontEnd.getBest;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.CONFIRM;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.ONE_TIME;
import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.signer4j.imp.SwingTools.invokeLater;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.gui.alert.MessageAlert;
import com.github.signer4j.imp.Threads;
import com.github.signer4j.imp.Threads.ShutdownHookThread;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.IPjeLifeCycleHook;
import br.jus.cnj.pje.office.core.IPjeOffice;
import br.jus.cnj.pje.office.core.imp.PJeOffice;
import br.jus.cnj.pje.office.core.imp.PjeConfig;

public class PjeOfficeApp implements IPjeLifeCycleHook {
  
  static {
    PjeConfig.setup();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(PjeOfficeApp.class);

  private IPjeOffice office;
  
  private IPjeFrontEnd frontEnd;

  private ShutdownHookThread jvmHook;
  
  public static void main(String[] args) {
    invokeLater(() ->  new PjeOfficeApp(getBest()).start());
  }

  private PjeOfficeApp(IPjeFrontEnd frontEnd) {
    this.office = new PJeOffice(this);
    this.frontEnd = frontEnd;
    this.jvmHook = Threads.shutdownHookAdd(office::exit, "JVMShutDownHook");
  }

  @Override
  public void onKill() {
    LOGGER.info("Liberando frontEnd");
    this.frontEnd.dispose();
    this.office = null;
    this.frontEnd = null;
    LOGGER.info("Reciclando jvmHook");
    Threads.shutdownHookRem(jvmHook);
    this.jvmHook = null;
    LOGGER.info("App closed");
  }

  @Override
  public void onFailStart(Exception e) {
    display("Uma versão antiga do PjeOffice precisa ser fechada e/ou desinstalada do seu computador.\n" + e.getMessage());
    System.exit(1);
  }

  private void start() {
    office.boot();
    
    final PopupMenu popup = new PopupMenu();

    MenuItem mnuConfig = new MenuItem("Configuração de certificado");
    mnuConfig.addActionListener(e -> office.showCertificates());

    MenuItem mnuSigner = new MenuItem("Assinador offline");
    mnuSigner.addActionListener(e -> office.showOfflineSigner());

    MenuItem mnuServer = new MenuItem("Servidores autorizados");
    mnuServer.addActionListener(e -> office.showAuthorizedServers());

    CheckboxMenuItem mnuOneTime = new CheckboxMenuItem(ONE_TIME.geLabel());
    CheckboxMenuItem mnuAways = new CheckboxMenuItem(AWAYS.geLabel());
    CheckboxMenuItem mnuConfirm = new CheckboxMenuItem(CONFIRM.geLabel());

    ItemListener listener = (e) -> {
      MenuItem item = (MenuItem)e.getSource();
      mnuAways.setState(item.getLabel().equals(AWAYS.geLabel()));
      mnuOneTime.setState(item.getLabel().equals(ONE_TIME.geLabel()));
      mnuConfirm.setState(item.getLabel().equals(CONFIRM.geLabel()));
      if (mnuAways.getState()) {
        office.setAuthStrategy(AWAYS);
      }else if (mnuOneTime.getState()) {
        office.setAuthStrategy(ONE_TIME);
      }else if (mnuConfirm.getState()) {
        office.setAuthStrategy(CONFIRM);
      }
    };

    mnuAways.addItemListener(listener);
    mnuAways.setState(office.isAwayStrategy());
    mnuOneTime.addItemListener(listener);
    mnuOneTime.setState(office.isOneTimeStrategy());
    mnuConfirm.addItemListener(listener);
    mnuConfirm.setState(office.isConfirmStrategy());
    
    Menu mnuSecurity = new Menu("Segurança");
    mnuSecurity.add(mnuAways);
    mnuSecurity.add(mnuOneTime);
    mnuSecurity.add(mnuConfirm);

    MenuItem mnuLog = new MenuItem("Registro de atividades");
    mnuLog.addActionListener(e -> office.showActivities());
    
    CheckboxMenuItem mnuDev = new CheckboxMenuItem("Ativar modo desenvolvedor");
    mnuDev.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED){
        mnuDev.setLabel("Desativar modo desenvolvedor");
        office.setDevMode();
      } else {
        mnuDev.setLabel("Ativar modo desenvolvedor");
        office.setProductionMode();
      }
    });

    MenuItem mnuExit   = new MenuItem("Sair");
    mnuExit.addActionListener(e ->  office.exit());

    Menu mnuOption = new Menu("Opções");
    mnuOption.add(mnuLog);

    if (PjeOfficeFrontEnd.supportsSystray()) {
      final IPjeFrontEnd front = frontEnd.next();
      MenuItem mnuDesk = new MenuItem(front.getTitle());
      mnuDesk.addActionListener(e -> {
        office.kill();
        new PjeOfficeApp(front).start();
      });
      mnuOption.add(mnuDesk);
    }
    
    mnuOption.add(mnuDev);

    popup.add(mnuConfig);
    popup.add(mnuSigner);
    popup.add(mnuServer);
    popup.addSeparator();
    popup.add(mnuSecurity);
    popup.addSeparator();
    popup.add(mnuOption);
    popup.addSeparator();
    popup.add(mnuExit);
    
    try {
      this.frontEnd.install(office, popup);
    } catch (Exception es) {
      LOGGER.error(this.frontEnd.getTitle() + "Não é suportada. Nova tentativa com Desktop", es);
      this.frontEnd = PjeOfficeFrontEnd.DESKTOP;
      try {
        this.frontEnd.install(office, popup);
      } catch (Exception ed) {
        String message = "Incapaz de instanciar frontEnd da aplicação.\n" + ed.getMessage();
        LOGGER.error(message, ed);
        MessageAlert.display(message);
        System.exit(1);
      }
    }
    Toolkit.getDefaultToolkit().beep();
  }
}
