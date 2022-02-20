
package br.jus.cnj.pje.office.imp;  

import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.CONFIRM;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.ONE_TIME;
import static com.github.signer4j.imp.Args.requireNonNull;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.github.signer4j.gui.alert.MessageAlert;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.imp.PjeCommandFactory;

public abstract class PjeOfficeClassic extends PjeOfficeApp {
  
  private IPjeFrontEnd frontEnd;

  protected PjeOfficeClassic(IPjeFrontEnd frontEnd, PjeCommandFactory factory, String... args) {
    super(factory, args);
    this.frontEnd = requireNonNull(frontEnd, "frontEnd is null");
  }

  @Override
  public void onKill() {
    LOGGER.info("Liberando frontEnd");
    this.frontEnd.dispose();
    this.frontEnd = null;
    super.onKill();
  }

  @Override
  protected void start() {
    super.start();
    
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
    
    CheckboxMenuItem mnuDev = new CheckboxMenuItem("Modo Treinamento");
    mnuDev.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED){
        office.setDevMode();
      } else {
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
        final String origin = office.getOrigin();
        office.kill();
        newInstance(front, origin).start();
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

  protected abstract PjeOfficeClassic newInstance(IPjeFrontEnd front, String origin);
}
