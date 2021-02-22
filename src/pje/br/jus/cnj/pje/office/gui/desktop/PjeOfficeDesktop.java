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

import com.github.signer4j.IExitable;
import com.github.signer4j.gui.utils.SimpleFrame;
import com.github.signer4j.imp.Config;

import br.jus.cnj.pje.office.core.Version;

public class PjeOfficeDesktop extends SimpleFrame {
  private static final long serialVersionUID = 1L;
  
  private JPanel contentPane;

  public PjeOfficeDesktop(IExitable office, PopupMenu popup) {
    super("PjeOffice - " + Version.current());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setBounds(100, 100, 336, 235);
    contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    JButton btnMain = new JButton("");
    btnMain.add(popup);
    btnMain.setIcon(new ImageIcon(Config.getIcon()));
    btnMain.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        office.exit();
      }
    });
    contentPane.add(btnMain, BorderLayout.CENTER);
    setLocationRelativeTo(null);
  }
  
  protected void onEscPressed(ActionEvent e) {
    ;//
  }
}
