package br.jus.cnj.pje.office.imp;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.jus.cnj.pje.office.IPjeFrotEnd;
import br.jus.cnj.pje.office.core.imp.IPjeOffice;
import br.jus.cnj.pje.office.gui.Images;
import br.jus.cnj.pje.office.gui.alert.MessageAlert;
import br.jus.cnj.pje.office.gui.desktop.PjeOfficeDesktop;

enum PjeOfficeFrontEnd implements IPjeFrotEnd {
  SYSTRAY {
    private SystemTray tray;
    private TrayIcon trayIcon;
    
    @Override
    public void install(IPjeOffice office, PopupMenu menu) throws Exception {
      this.tray = SystemTray.getSystemTray();
      this.trayIcon = new TrayIcon(Images.PJE_ICON_TRAY.asImage());
      this.trayIcon.setPopupMenu(menu);
      this.trayIcon.addActionListener(e -> MessageAlert.display("Menu acessível com botão auxiliar do mouse"));
      this.tray.add(trayIcon);
    }

    @Override
    public void dispose() {
      tray.remove(trayIcon);
    }
  },
  DESKTOP {
    private PjeOfficeDesktop desktop;
    
    @Override
    public void install(IPjeOffice office, PopupMenu menu) {
      this.desktop = new PjeOfficeDesktop(office, menu);
      this.desktop.showToFront();
    }

    @Override
    public void dispose() {
      this.desktop.close();
    }
  };
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeOfficeFrontEnd.class);
  
  public static PjeOfficeFrontEnd getBest() {
    boolean systray = SystemTray.isSupported();
    LOGGER.info("Suporte a systray: " + systray);
    boolean forceDesktop = System.getenv("PJE_OFFICE_DESKTOP") != null;
    LOGGER.info("Forçar uso desktop: " + forceDesktop);
    return systray && !forceDesktop ? SYSTRAY : DESKTOP;
  }
}