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


package br.jus.cnj.pje.office.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.awt.Frame;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.utils4j.imp.Threads;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.gui.PjeImages;
import br.jus.cnj.pje.office.gui.desktop.PjeOfficeDesktop;

enum PjeOfficeFrontEnd implements IPjeFrontEnd {
  
  SYSTRAY("Versão Systray") {
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Frame trayFrame;
    
    @Override
    public void install(IBootable office, PopupMenu menu) throws Exception {
      trayFrame = new Frame("");
      trayFrame.setType(Type.UTILITY);
      trayFrame.setUndecorated(true);
      trayFrame.setResizable(false);
      trayFrame.setVisible(true);
      tray = SystemTray.getSystemTray();
      trayIcon = new TrayIcon(PjeImages.PJE_ICON_TRAY.asImage().orElseThrow(IconNotFoundException::new));
      trayIcon.setPopupMenu(menu);
      trayIcon.addMouseListener(new MouseAdapter() {
        public void mouseReleased(MouseEvent e) {
          if (e.getButton() != MouseEvent.BUTTON3) {
            trayFrame.add(menu);
            menu.show(trayFrame, e.getX(), e.getY());
            trayFrame.removeAll();
          }
        }
      });
      tray.add(trayIcon);
      trayIcon.setToolTip("PjeOffice - Assinador do Pje.");
      trayIcon.displayMessage("PjeOffice PRO", "Versão " + Version.current().toString(), TrayIcon.MessageType.NONE);
    }

    @Override
    protected void doDispose() {
      LOGGER.debug("Removendo trayIcon de tray");
      if (tray != null) {
        tryRun(() -> tray.remove(trayIcon));
        tray = null;
      }
      LOGGER.debug("Anulando PopupMenu em trayIcon");
      if (trayIcon != null) {
        tryRun(() -> trayIcon.setPopupMenu(null));
        trayIcon = null;
      }
      LOGGER.debug("Removendo components do frame utilitario");
      if (trayFrame != null) {
        tryRun(trayFrame::removeAll);
        trayFrame = null;
      }
      LOGGER.debug("systray disposed!");
    }

    @Override
    public IPjeFrontEnd next() {
      return DESKTOP;
    }
  },
  
  DESKTOP("Versão Desktop") {
    private PjeOfficeDesktop desktop;
    
    @Override
    public void install(IBootable office, PopupMenu menu) {
      desktop = new PjeOfficeDesktop(office, menu);
      desktop.showToFront();
    }

    @Override
    protected void doDispose() {
      if (desktop != null) {
        tryRun(desktop::close);
      }
      desktop = null;
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
