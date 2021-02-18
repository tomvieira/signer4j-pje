package br.jus.cnj.pje.office.gui.certlist;

import static br.jus.cnj.pje.office.core.imp.PJeConfigPersister.CONF;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.github.signer4j.ICertificateList;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.GuiTools;

import br.jus.cnj.pje.office.gui.Images;
import br.jus.cnj.pje.office.gui.PjeDialog;
import br.jus.cnj.pje.office.gui.certlist.PjeCertificateInstaller.IPjeA1A3ConfigSaved;

public class PjeCertificateList extends PjeDialog implements ICertificateList {

  private static final long serialVersionUID = -1L;

  private JPanel contentPane;
  private JTable table;
  private JPanel pnlCenter;
  private JPanel pnlSouth;
  private JPanel pnlSouthInner;
  private JCheckBox chkRememberMe;
  private JPanel btnButtons;
  private JButton btnOk;
  private JButton btnCancel;
  private JPanel pnlNorth;
  private JLabel lblCertificateList;
  private JLabel lblConfigInstall;
  private final String defaultAlias;
  private final IPjeA1A3ConfigSaved onSaved;
  
  private Optional<ICertificateEntry> selectedEntry = Optional.empty();

  public PjeCertificateList(String defaultAlias, IPjeA1A3ConfigSaved onSaved) {
    super("Seleção de certificado", true);
    this.defaultAlias = Args.requireNonNull(defaultAlias, "defaultAlias is null");
    this.onSaved = onSaved;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 560, 287);
    contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    pnlCenter = new JPanel();
    contentPane.add(pnlCenter, BorderLayout.CENTER);
    pnlCenter.setLayout(new CardLayout(0, 0));
    table = new JTable();
    table.setModel(new CertificateModel());
    table.getColumnModel().getColumn(0).setPreferredWidth(70);
    table.getColumnModel().getColumn(1).setPreferredWidth(167);
    table.getColumnModel().getColumn(2).setPreferredWidth(132);
    table.getColumnModel().getColumn(3).setPreferredWidth(111);
    table.getColumnModel().getColumn(3).setMinWidth(111);
    TableCellRenderer renderer = (TableCellRenderer) table.getTableHeader().getDefaultRenderer();
    ((DefaultTableCellRenderer)renderer).setHorizontalAlignment(JLabel.LEFT);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFont(new Font("Tahoma", Font.PLAIN, 13));
    table.setFillsViewportHeight(true);
    table.setBorder(null);
    table.getSelectionModel().addListSelectionListener((e) -> {
      int selectedRow = table.getSelectedRow();
      boolean enabled = false;
      if (selectedRow < 0) {
        this.selectedEntry = Optional.empty();
        this.chkRememberMe.setSelected(false);
      }else {
        CertificateModel model = (CertificateModel)table.getModel();
        ICertificateEntry rowEntry = model.getEntryAt(selectedRow);
        this.selectedEntry = Optional.of(rowEntry);
        enabled |= rowEntry.isValid();
        this.chkRememberMe.setSelected(defaultAlias.equals(rowEntry.getId()) && enabled);
        rowEntry.setRemembered(this.chkRememberMe.isSelected());
      }
      btnOk.setEnabled(enabled);
      chkRememberMe.setEnabled(enabled);
    });

    JScrollPane scrollPane = new JScrollPane(table);
    pnlCenter.add(scrollPane);

    pnlSouth = new JPanel();
    contentPane.add(pnlSouth, BorderLayout.SOUTH);
    pnlSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

    pnlSouthInner = new JPanel();
    pnlSouth.add(pnlSouthInner);
    pnlSouthInner.setLayout(new GridLayout(0, 2, 0, 0));

    chkRememberMe = new JCheckBox("Memorizar e não perguntar novamente");
    chkRememberMe.setEnabled(false);
    chkRememberMe.setSelected(false);
    pnlSouthInner.add(chkRememberMe);

    btnButtons = new JPanel();
    pnlSouthInner.add(btnButtons);
    btnButtons.setLayout(new GridLayout(1, 0, 30, 0));

    btnOk = new JButton("OK");
    btnOk.setEnabled(false);
    btnOk.addActionListener((arg) -> close());
    btnButtons.add(btnOk);

    btnCancel = new JButton("Cancelar");
    btnCancel.addActionListener((e) -> clickCancel(e));
    btnButtons.add(btnCancel);

    pnlNorth = new JPanel();
    contentPane.add(pnlNorth, BorderLayout.NORTH);
    pnlNorth.setLayout(new BorderLayout(0, 0));

    lblCertificateList = new JLabel("Certificados Disponíveis");
    lblCertificateList.setIcon(Images.PJE_CERTIFICATE.asIcon());
    lblCertificateList.setHorizontalAlignment(SwingConstants.LEFT);
    lblCertificateList.setFont(new Font("Tahoma", Font.BOLD, 15));
    pnlNorth.add(lblCertificateList);

    lblConfigInstall = new JLabel("<html><u>Configurar um novo certificado&nbsp;&nbsp;</u>");
    lblConfigInstall.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lblConfigInstall.setForeground(Color.RED);
    lblConfigInstall.setFont(new Font("Tahoma", Font.ITALIC, 12));
    lblConfigInstall.setVerticalAlignment(SwingConstants.BOTTOM);
    lblConfigInstall.setHorizontalAlignment(SwingConstants.RIGHT);
    lblConfigInstall.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        clickConfig();
      }
    });
    lblConfigInstall.setVisible(onSaved != null);
    pnlNorth.add(lblConfigInstall, BorderLayout.EAST);
    setLocationRelativeTo(null);
    GuiTools.mouseTracker(this);
  }

  private static class CertificateModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private List<ICertificateEntry> entries;

    public CertificateModel() {
      this.entries = new LinkedList<>();
    }

    @Override
    public int getRowCount() {
      return entries.size();
    }

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }

    @Override
    public String getColumnName(int columnIndex) {
      switch (columnIndex){
      case 0:
        return "Dispositivo";
      case 1:
        return "Nome";
      case 2:
        return "Emitido Por";
      case 3:
        return "Validade";
      }
      return "?";  
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      ICertificateEntry entry = entries.get(rowIndex);
      switch (columnIndex) {
      case 0:
        return entry.getDevice();
      case 1:
        return entry.getName();
      case 2:
        return entry.getIssuer();
      case 3:
        return entry.getDate();
      }
      return "?";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return String.class;
    }

    public ICertificateEntry getEntryAt(int row) {
      return entries.get(row);
    }
    
    public void clear() {
      entries.clear();
      fireTableDataChanged();
    }

    public void load(List<ICertificateEntry> entry) {
      clear();
      int i = 0;
      while(i < entry.size()) {
        this.entries.add(entry.get(i));
        fireTableRowsInserted(i, i);
        i++;
      }
    }
  }
  
  boolean needReload = false;
  private void clickConfig() {
    new PjeCertificateInstaller((a, b) -> {
      this.needReload = true;
      this.table.getSelectionModel().clearSelection();
      if (this.onSaved != null) {
        this.onSaved.execute(a, b);
      }
    }).setVisible(true);
    if (this.needReload) {
      this.close();
    }
  }

  private void clickCancel(ActionEvent e) {
    ((CertificateModel)table.getModel()).clear();
    this.selectedEntry = Optional.empty();
    this.close();
  }

  @Override
  public Optional<ICertificateEntry> show(List<ICertificateEntry> entries) {
    Args.requireNonNull(entries, "entries is null");
    this.chkRememberMe.setSelected(false);
    this.chkRememberMe.setEnabled(false);
    ((CertificateModel)table.getModel()).load(entries);
    this.setVisible(true);
    if (this.selectedEntry.isPresent()) {
      ICertificateEntry selectedEntry = this.selectedEntry.get();
      if (this.defaultAlias.equals(selectedEntry.getId())) {
        if (!this.chkRememberMe.isSelected())
          CONF.save("");
      }else if (this.chkRememberMe.isSelected()) {
          CONF.save(selectedEntry.getId());
      }
    }
    this.close();
    return needReload ? null : this.selectedEntry; 
  }
  
  public static Optional<ICertificateEntry> display(List<ICertificateEntry> entries) {
    return display(entries, null, true);
  }

  public static Optional<ICertificateEntry> display(List<ICertificateEntry> entries, IPjeA1A3ConfigSaved onSaved, boolean auto) {
    Args.requireNonNull(entries, "entries is null");
    String defaultAlias = CONF.defaultAlias().orElse("$not_found$");
    if (auto) {
      Optional<ICertificateEntry> defaultEntry = entries
          .stream()
          .filter(c -> c.getId().equals(defaultAlias)) //auto select!
          .findFirst();
      if (defaultEntry.isPresent()) {
        return defaultEntry;
      }
    }
    return new PjeCertificateList(defaultAlias, onSaved).show(entries);
  }
}
