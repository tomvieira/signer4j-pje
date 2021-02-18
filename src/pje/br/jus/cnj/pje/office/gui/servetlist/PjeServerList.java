package br.jus.cnj.pje.office.gui.servetlist;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.github.signer4j.gui.utils.ButtonRenderer;
import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.gui.Images;
import br.jus.cnj.pje.office.gui.PjeDialog;

public class PjeServerList extends PjeDialog implements IPjeServerList {

  private static final long serialVersionUID = 1L;

  private final JPanel contentPane;
  
  private final JTable table;
  
  private List<IServerEntry> currentList = Collections.emptyList();
  private List<IServerEntry> loadedList;

  public PjeServerList() {
    super("Servidores autorizados", true);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 602, 371);
    contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel pnlNorth = new JPanel();
    contentPane.add(pnlNorth, BorderLayout.NORTH);
    pnlNorth.setLayout(new BorderLayout(0, 0));

    JLabel lblServerList = new JLabel("Servidores Disponíveis");
    lblServerList.setIcon(Images.PJE_SERVER.asIcon());
    lblServerList.setHorizontalAlignment(SwingConstants.LEFT);
    lblServerList.setFont(new Font("Tahoma", Font.BOLD, 15));
    pnlNorth.add(lblServerList, BorderLayout.NORTH);

    JPanel pnlCenter = new JPanel();
    contentPane.add(pnlCenter, BorderLayout.CENTER);
    pnlCenter.setLayout(new CardLayout(0, 0));

    table = new JTable();
    table.setDefaultEditor(Authorization.class, new AuthorizationEditor());
    table.setDefaultRenderer(Authorization.class, new AuthorizationEditor());
    table.setModel(new ServerModel());
    table.getColumnModel().getColumn(0).setPreferredWidth(40);
    table.getColumnModel().getColumn(1).setPreferredWidth(190);
    table.getColumnModel().getColumn(2).setPreferredWidth(102);
    
    ButtonRenderer bc = new ButtonRenderer((arg) -> clickRemove(arg));
    table.getColumnModel().getColumn(3).setCellRenderer( bc );
    table.getColumnModel().getColumn(3).setCellEditor(bc);
        
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFont(new Font("Tahoma", Font.PLAIN, 13));
    table.setFillsViewportHeight(true);
    table.setBorder(null);
    table.setRowHeight(table.getRowHeight() + 10);
    
    TableCellRenderer renderer = (TableCellRenderer)table.getTableHeader().getDefaultRenderer();
    ((DefaultTableCellRenderer)renderer).setHorizontalAlignment(JLabel.LEFT);
    JScrollPane scrollPane = new JScrollPane(table);
    pnlCenter.add(scrollPane);

    JPanel pnlSouth = new JPanel();
    contentPane.add(pnlSouth, BorderLayout.SOUTH);
    pnlSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

    JPanel pnlSouthInner = new JPanel();
    pnlSouth.add(pnlSouthInner);
    pnlSouthInner.setLayout(new GridLayout(0, 1, 0, 0));

    JPanel btnButtons = new JPanel();
    pnlSouthInner.add(btnButtons);
    btnButtons.setLayout(new GridLayout(1, 0, 30, 0));

    JButton btnOk = new JButton("OK");
    btnOk.addActionListener((e) -> clickOK(e));
    btnButtons.add(btnOk);

    JButton btnCancel = new JButton("Cancelar");
    btnCancel.addActionListener((a) -> clickCancel(a));
    btnButtons.add(btnCancel);
    
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent e) {
        clickCancel(null);
      }
    });
    setLocationRelativeTo(null);
  }
  
  @Override
  protected void onEscPressed(ActionEvent e) {
    clickCancel(e);
  }
  
  private void clickCancel(ActionEvent e) {
    currentList = loadedList;
    this.close();
  }
  
  private void clickOK(ActionEvent e) {
    ServerModel model = (ServerModel)table.getModel();
    if (model.getRowCount() == 0) {
      currentList = Collections.emptyList();
    } else {
      currentList = model.getList();
    }
    this.close();
  }
  
  private void clickRemove(ActionEvent arg) {
    ServerModel model = (ServerModel)table.getModel();
    int row = table.getSelectedRow();
    if (row >= 0) {
      model.remove(row);
    }
  }
  
  @Override
  public final List<IServerEntry> show(List<IServerEntry> entries) {
    Args.requireNonNull(entries, "entries is null");
    this.loadedList = clone(entries);
    ServerModel model = (ServerModel)table.getModel();
    model.load(entries);
    setVisible(true);
    return currentList;
  }
  
  private static List<IServerEntry> clone(List<IServerEntry> entries) {
    return entries.stream().map(se -> se.clone()).collect(Collectors.toList());
  }

  private static final class ServerModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = new String[] {
      "Aplicação", 
      "Servidor", 
      "Autorizado", 
      "Ação"
    };
    
    private static final long serialVersionUID = 1L;
    
    private final List<IServerEntry> entries;

    ServerModel() {
      entries = new ArrayList<IServerEntry>();
    }

    public final List<IServerEntry> getList() {
      return new ArrayList<>(entries); //copy of list (defensive programming)
    }

    public final void clear() {
      entries.clear();
      fireTableDataChanged();
    }
    
    public final void load(List<IServerEntry> entry) {
      clear();
      int i = 0;
      while(i < entry.size()) {
        this.entries.add(entry.get(i));
        fireTableRowsInserted(i, i);
        i++;
      }
    }

    public final void remove(int index) {
      if (index < 0 || index >= entries.size())
        return;
      entries.remove(index);
      fireTableRowsDeleted(index, index);
    }
    
    public final int getRowCount() {
      return entries.size();
    }

    @Override
    public final String getColumnName(int column) {
      return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
      case 0:
      case 1:
        return String.class;
      case 2:
        return Authorization.class;
      case 3:
        return Action.class;
      default:
        return Object.class;
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex >= 2;
    }

    @Override
    public int getColumnCount() {
      return COLUMN_NAMES.length;
    }
    
    @Override
    public void setValueAt(Object aValue, int rIndex, int cIndex) {
      IServerEntry entry = (IServerEntry) entries.get(rIndex);
      switch (cIndex) {
      case 2:
        entry.setAuthorization((Authorization)aValue);
        break;
      default:
      }
      fireTableCellUpdated(rIndex, cIndex);
    };

    @Override
    public Object getValueAt(int rIndex, int cIndex) {
      IServerEntry entry = (IServerEntry) entries.get(rIndex);
      switch (cIndex) {
      case 0:
        return entry.getApp();
      case 1:
        return entry.getServer();
      case 2:
        return entry.getAuthorization();
      case 3:
        return entry.getAction();
      }
      return null;
    }
  }

  private class AuthorizedPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;

    private JRadioButton yesOption;
    private JRadioButton noOption;

    private ButtonGroup buttonGroup = new ButtonGroup();

    AuthorizedPanel(ActionListener listener) {
      super(new GridLayout(1, 3));
      setOpaque(true);
      yesOption = createRadio(Authorization.SIM, listener);
      noOption = createRadio(Authorization.NÃO, listener);
    }

    private JRadioButton createRadio(Authorization status, ActionListener listener) {
      JRadioButton radioButton = new JRadioButton(status.name());
      radioButton.addActionListener(listener);
      radioButton.setOpaque(false);
      add(radioButton);
      buttonGroup.add(radioButton);
      return radioButton;
    }

    public Authorization getAuthorization() {
      if (yesOption.isSelected())
        return Authorization.SIM;
      return Authorization.NÃO;
    }

    public void setAuthorization(Authorization status) {
      yesOption.setSelected(Authorization.SIM.equals(status));
      noOption.setSelected(Authorization.NÃO.equals(status));
    }
  }


  private class AuthorizationEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener  {
    
    private static final long serialVersionUID = 1L;

    private final AuthorizedPanel authorizationPanel;

    AuthorizationEditor() {
      authorizationPanel = new AuthorizedPanel(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      authorizationPanel.setAuthorization((Authorization)value);
      if (isSelected) {
        authorizationPanel.setBackground(table.getSelectionBackground());
      } else {
        authorizationPanel.setBackground(table.getBackground());
      }
      return authorizationPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      stopCellEditing();      
    }
    
    @Override
    public Object getCellEditorValue() {
      return authorizationPanel.getAuthorization();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,  int row, int column) {
      authorizationPanel.setAuthorization((Authorization)value);
      if (isSelected) {
        authorizationPanel.setBackground(table.getSelectionBackground());
      } else {
        authorizationPanel.setBackground(table.getBackground());
      }
      return authorizationPanel;
    }
  }
}
