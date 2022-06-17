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

import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.AWAYS;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.CONFIRM;
import static br.jus.cnj.pje.office.signer4j.imp.PjeAuthStrategy.ONE_TIME;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.IPjeFrontEnd;
import br.jus.cnj.pje.office.core.imp.PjeLifeCycleFactory;

public abstract class PjeOfficeClassic extends PjeOfficeApp {
  
  private IPjeFrontEnd frontEnd;

  protected PjeOfficeClassic(IPjeFrontEnd frontEnd, PjeLifeCycleFactory factory, String... args) {
    super(factory, args);
    this.frontEnd = Args.requireNonNull(frontEnd, "frontEnd is null");
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

    MenuItem mnuServer = new MenuItem("Servidores autorizados");
    mnuServer.addActionListener(e -> office.showAuthorizedServers());

    MenuItem mnuSelect = new MenuItem("Selecionar arquivos para...");   
    mnuSelect.addActionListener(e -> office.selectTo());    
    
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

//    MenuItem mnuLog = new MenuItem("Registro de atividades");
//    mnuLog.addActionListener(e -> office.showActivities());
    
    CheckboxMenuItem mnuDev = new CheckboxMenuItem("Modo inseguro (evite usar)");
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
    //mnuOption.add(mnuLog);

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

    popup.add(mnuSelect);
    popup.addSeparator();
    popup.add(mnuConfig);
    popup.add(mnuServer);
    popup.add(mnuSecurity);
    popup.addSeparator();
    popup.add(mnuOption);
    popup.addSeparator();
    popup.add(mnuExit);
    
    try {
      this.frontEnd.install(office, popup);
    } catch (Exception es) {
      this.frontEnd.dispose();
      LOGGER.error(this.frontEnd.getTitle() + "Não é suportada. Nova tentativa com Desktop", es);
      this.frontEnd = PjeOfficeFrontEnd.DESKTOP;
      try {
        this.frontEnd.install(office, popup);
      } catch (Exception ed) {
        this.frontEnd.dispose();
        String message = "Incapaz de instanciar frontEnd da aplicação.";
        LOGGER.error(message, ed);
        Dialogs.error(message);
        System.exit(1);
      }
    }
    Toolkit.getDefaultToolkit().beep();
  }

  protected abstract PjeOfficeClassic newInstance(IPjeFrontEnd front, String origin);
}
