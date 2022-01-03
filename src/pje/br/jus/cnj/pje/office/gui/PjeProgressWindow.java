package br.jus.cnj.pje.office.gui;

import static com.github.signer4j.imp.SwingTools.invokeLater;
import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.gui.utils.SimpleFrame;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Objects;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressFactory;

import br.jus.cnj.pje.office.core.IPjeProgressView;
import io.reactivex.disposables.Disposable;

class PjeProgressWindow extends SimpleFrame implements IPjeProgressView {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(PjeProgressWindow.class);

  private final IProgress progress;
  
  private final JPanel contentPane        = new JPanel();
  private final JTextArea textArea        = new JTextArea();
  private final JProgressBar progressBar  = new JProgressBar();
  private volatile Thread threadContext;

  private Disposable stepToken, stageToken; 

  PjeProgressWindow(IProgress progress) {
    super("Progresso");
    this.progress = Args.requireNonNull(progress, "progress is null");
    setBounds(100, 100, 450, 154);
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel pngNorth = new JPanel();
    contentPane.add(pngNorth, BorderLayout.NORTH);
    pngNorth.setLayout(new GridLayout(3, 1, 0, 0));

    JLabel lblLog = new JLabel("Registro de atividades");
    lblLog.setIcon(PjeImages.PJE_LOG.asIcon());
    lblLog.setHorizontalAlignment(SwingConstants.LEFT);
    lblLog.setFont(new Font("Tahoma", Font.BOLD, 15));
    pngNorth.add(lblLog);

    pngNorth.add(progressBar);
    resetProgress();

    final JPanel pnlSouth = new JPanel();
    
    JLabel lbldetalhes = new JLabel("Ver detalhes   ");
    lbldetalhes.setVerticalAlignment(SwingConstants.BOTTOM);
    lbldetalhes.setHorizontalAlignment(SwingConstants.RIGHT);
    lbldetalhes.setForeground(Color.RED);
    lbldetalhes.setFont(new Font("Tahoma", Font.ITALIC, 12));
    lbldetalhes.addMouseListener(new MouseAdapter(){  
      public void mouseClicked(MouseEvent e) {
        boolean show = lbldetalhes.getText().contains("Ver");
        if (show) {
          setBounds(getBounds().x, getBounds().y, 450, 312);
          lbldetalhes.setText("Esconder detalhes   ");
        }else {
          setBounds(getBounds().x, getBounds().y, 450, 154);
          lbldetalhes.setText("Ver detalhes   ");
        }
        pnlSouth.setVisible(show);
      }
    });
    pngNorth.add(lbldetalhes);
    
    contentPane.add(pnlSouth, BorderLayout.SOUTH);
    pnlSouth.setLayout(new GridLayout(0, 3, 10, 0));

    JSeparator separator = new JSeparator();
    pnlSouth.add(separator);

    JButton btnLimpar = new JButton("Limpar");
    btnLimpar.addActionListener((e) -> clickClear(e));
    pnlSouth.add(btnLimpar);

    JButton btnNewButton = new JButton("Cancelar");
    btnNewButton.addActionListener((e) -> clickCancel(e));
    pnlSouth.add(btnNewButton);
    pnlSouth.setVisible(false);

    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);

    textArea.setRows(8);
    textArea.setEditable(false);
    scrollPane.setViewportView(textArea);
    setLocationRelativeTo(null);
    setAutoRequestFocus(true);
  }
  
  @Override
  protected void onEscPressed(ActionEvent e) {
    clickCancel(e);
  }
  

  private void clickCancel(ActionEvent e) {
    if (threadContext != null) {
      int reply = JOptionPane.showConfirmDialog(null, 
        "Deseja mesmo cancelar a operação?", 
        "Cancelamento da operação", 
        JOptionPane.YES_NO_OPTION
      );
      if (reply != JOptionPane.YES_OPTION) {
        return;
      }
      if (threadContext != null) { //double check!
        threadContext.interrupt();
      }
    }
    this.undisplay();
  }

  private void clickClear(ActionEvent e) {
    textArea.setText("");
  }

  @Override
  public void display() {
    invokeLater(() -> { 
      this.setLocationRelativeTo(null);
      this.showToFront(); 
    });
  }

  @Override
  public void undisplay() {
    invokeLater(() -> {
      this.setVisible(false);
      this.resetProgress();
    });
  }

  private void resetProgress() {
    this.progressBar.setIndeterminate(false);
    this.progressBar.setStringPainted(true);
    this.progressBar.setString("");
    this.threadContext = null;
  }

  private void checkDispose() {
    if (stepToken != null)
      stepToken.dispose();
    if (stageToken != null)
      stageToken.dispose();
  } 

  @Override
  public void dispose() {
    checkDispose();
    super.dispose();
  }

  private static String computeTabs(int stackSize) {
    StringBuilder b = new StringBuilder(6);
    while(stackSize-- > 0)
      b.append("  ");
    return b.toString();
  }

  public IProgressFactory getProgressFactory() {
    return () -> {
      checkDispose();
      progress.reset(() -> threadContext = null);
      progress.setThread((Thread t) -> threadContext = t);
      progress.applyThread();
      
      stepToken = progress.stepObservable().subscribe(e -> {
        progressBar.setIndeterminate(true);
        final int step = e.getStep();
        final int total = e.getTotal();
        final String message = e.getMessage();
        final StringBuilder text = new StringBuilder(computeTabs(e.getStackSize()));

        SwingUtilities.invokeLater(() -> {
          String log;
          if (total > 0) {//TODO ativar o recurso de not indeterminate!
            //            progressBar.setMinimum(0);
            //            progressBar.setMaximum(total);
            //            progressBar.setValue(step);
            log = text.append(format("Passo %s de %s: %s", Objects.arrayOf(step, total, message))).toString();
          } else if (step > 0){
            log = text.append(message).toString();//format("Passo %s: %s", step, message)).toString();
          } else {
            log = text.append(message).toString();
          }
          progressBar.setString(log);
          textArea.append(log + "\n\r");
          LOGGER.info(log); 
        });

      });
      stageToken = progress.stageObservable().subscribe(e -> {
        String tabSize = computeTabs(e.getStackSize());
        String text = tabSize + e.getMessage();
        SwingUtilities.invokeLater(() -> {
          progressBar.setIndeterminate(true);
          progressBar.setString(text);
          textArea.append(text + "\n\r");
          LOGGER.info(text);
        });
      });
      return progress;
    };
  }
}
