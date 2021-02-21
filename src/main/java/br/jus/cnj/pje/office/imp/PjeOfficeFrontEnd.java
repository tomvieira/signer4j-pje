package br.jus.cnj.pje.office.imp;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Threads;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.IPjeOffice;
import br.jus.cnj.pje.office.gui.Images;
import br.jus.cnj.pje.office.gui.alert.MessageAlert;
import br.jus.cnj.pje.office.gui.desktop.PjeOfficeDesktop;

enum PjeOfficeFrontEnd implements IPjeFrontEnd {
  SYSTRAY("Versão Systray") {
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
    public void doDispose() {
      LOGGER.debug("Anulando PopupMenu em trayIcon");
      trayIcon.setPopupMenu(null);
      LOGGER.debug("Removendo trayIcon de tray");
      tray.remove(trayIcon);
      LOGGER.debug("Anulando atributos trayIcon e tray");
      trayIcon = null;
      tray = null;
    }

    @Override
    public IPjeFrontEnd next() {
      return DESKTOP;
    }
  },
  DESKTOP("Versão Desktop") {
    private PjeOfficeDesktop desktop;
    
    @Override
    public void install(IPjeOffice office, PopupMenu menu) {
      this.desktop = new PjeOfficeDesktop(office, menu);
      this.desktop.showToFront();
    }

    @Override
    public void doDispose() {
      this.desktop.close();
      this.desktop = null;
    }

    @Override
    public IPjeFrontEnd next() {
      return SYSTRAY;
    }
  };
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PjeOfficeFrontEnd.class);
  
  private String title;
  
  PjeOfficeFrontEnd(String title) {
    this.title = title;
  }
  
  @Override
  public final String getTitle() {
    return title;
  }
  
  @Override
  public final void dispose() {
    if (Threads.isShutdownHook()) {
      LOGGER.info("Dispose escaped (thread em shutdownhook)");
      return;
    }
    doDispose();
    LOGGER.info("Frontend liberado");
  }
  
  protected abstract void doDispose();
  
  public static PjeOfficeFrontEnd getBest() {
    boolean systray = supportsSystray();
    LOGGER.info("Suporte a systray: " + systray);
    boolean forceDesktop = System.getenv("PJE_OFFICE_DESKTOP") != null;
    LOGGER.info("Forçar uso desktop: " + forceDesktop);
    return systray && !forceDesktop ? SYSTRAY : DESKTOP;
  }

  public static boolean supportsSystray() {
    return SystemTray.isSupported();
  }
}