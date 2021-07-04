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


package br.jus.cnj.pje.office.gui.desktop;

import java.awt.BorderLayout;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.github.utils4j.gui.imp.SimpleFrame;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.core.imp.PjeConfig;
import br.jus.cnj.pje.office.gui.PjeImages;

public class PjeOfficeDesktop extends SimpleFrame {
  private static final long serialVersionUID = 1L;
  
  private JPanel contentPane; 
  private JButton btnMain;

  public PjeOfficeDesktop(IBootable boot, PopupMenu popup) {
    super("PjeOffice - " + Version.current(), PjeConfig.getIcon());
    contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    ImageIcon viewport = new ImageIcon(PjeConfig.getIcon());
    btnMain = new JButton("");
    btnMain.add(popup);
    btnMain.setIcon(viewport);
    btnMain.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        boot.exit();
      }
    });
    contentPane.add(btnMain, BorderLayout.CENTER);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setResizable(false);
    setBounds(100, 100, viewport.getIconWidth(), viewport.getIconHeight());
    setContentPane(contentPane);
    setLocationRelativeTo(null);
  }
  
  public void setOnline(boolean online) {
    btnMain.setIcon(online ? PjeImages.PJE_ICON_ONLINE.asIcon().get() : PjeImages.PJE_ICON.asIcon().get());
  }
  
  @Override
  protected void onEscPressed(ActionEvent e) {
    ;//nothing to do (escape esc key)
  }

}
