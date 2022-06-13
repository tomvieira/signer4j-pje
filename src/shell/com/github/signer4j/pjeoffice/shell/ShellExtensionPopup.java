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

package com.github.signer4j.pjeoffice.shell;

import static com.github.utils4j.imp.Threads.startAsync;
import static java.util.stream.Collectors.toList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.github.progress4j.IProgressView;
import com.github.progress4j.IStage;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.imp.SignatureType;
import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.gui.imp.SimpleFrame;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Booleans;

import br.jus.cnj.pje.office.core.imp.PjeProgressFactory;
import br.jus.cnj.pje.office.task.imp.AssinaturaPadrao;
import br.jus.cnj.pje.office.task.imp.PjeSignMode;
import br.jus.cnj.pje.office.task.imp.PjeTaskReader;
import net.miginfocom.swing.MigLayout;

public class ShellExtensionPopup extends SimpleFrame {
  private static final long serialVersionUID = 1L;
  
  public static void show(List<File> files) {
    new ShellExtensionPopup(files).showToFront();
  }

  private static List<File> getItems(List<File> files, String extension) {
    return files.stream().filter(f -> f.getName().toLowerCase().endsWith(extension)).collect(toList());
  }

  private static class ActionButton extends JButton {
    private static final long serialVersionUID = 1L;

    ActionButton(String label, ActionListener action) {
      super(label);
      setHorizontalAlignment(SwingConstants.LEFT);
      addActionListener(action);
    }
  }

  private static enum Stage implements IStage {
    ANALYZING("Analisando");
    
    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }

  private final List<File> pdfs;
  private final List<File> mp4s;
  
  private ShellExtensionPopup(List<File> files) {
    super("Ações");
    Args.requireNonEmpty(files, "files is empty");
    this.pdfs = getItems(files, ".pdf");
    this.mp4s  = getItems(files, ".mp4");
    createLayout();
    setLocationRelativeTo(null);
  }


  private void createLayout() {
    setLayout(new MigLayout("wrap", "[]", "[]"));
    createActions();
    setResizable(false);
    setAlwaysOnTop(true);
    pack();    
  } 

  private void createActions() {
    createPdfActions();
    createMp4Actions();
  }

  private void addAction(String label, ActionListener action) {
    add(new ActionButton(label, action), "growx");
  }

  private void createSigningActions() {
    addAction("Assinar e salvar na mesma pasta", this::signAtSameFolder);
    addAction("Assinar e salvar em nova pasta", this::signAtNewFolder);
    addAction("Assinar e salvar em pasta específica...", this::signAtOtherFolder);
  }
    
  private void createPdfActions() {
    if (pdfs.isEmpty())
      return;
    createSigningActions();    
    addAction("Gerar 1 pdf a cada 10MB (Pje)", this::pdfSplit10);
    addAction("Gerar 1 pdf a cada 'n'MB...", this::pdfSplitN);
    addAction("Gerar 1 pdf por página", this::pdfSplitCount1);
    addAction("Gerar 1 pdf a cada 'n' páginas", this::pdfSplitCountN);
    addAction("Gerar 1 pdf com as páginas ÍMPARES", this::pdfSplitByI);
    addAction("Gerar 1 pdf com as páginas PARES", this::pdfSplitByP);
    addAction("Gerar pdf's com páginas específicas...", this::pdfSplitByPages);
    if (pdfs.size() > 1) { 
      addAction("Unir pdf's selecionados", this::pdfJoin);
    }
  }

  private void createMp4Actions() {
    if (mp4s.isEmpty())
      return;
    addAction("Gerar 1 vídeo a cada 90MB (Pje)", this::mp4Split90);
    addAction("Gerar 1 vídeo a cada 'n'MB...", this::mp4SplitNSize);
    addAction("Gerar 1 vídeo a cada 'n' minutos", this::mp4SplitNTime);
    addAction("Gerar cortes específicos...", this::mp4Slice);
    addAction("Extrair audio OGG", this::mp4Audio);
    addAction("Converter para WEBM", this::mp4Webm);
  }
  
  private void mp4Split90(ActionEvent action) {
    mp4SplitBySize(action, 90);
  }

  private void mp4SplitNSize(ActionEvent action) {
    mp4SplitBySize(action, 0);
  }

  private void mp4SplitNTime(ActionEvent action) {
    mp4SplitByDuration(action, 0);    
  }

  private void signAtSameFolder(ActionEvent e) {
    sign(e, "samefolder");
  }

  private void signAtNewFolder(ActionEvent e) {
    sign(e, "newfolder");  
  }
  
  private void signAtOtherFolder(ActionEvent e) {
    sign(e, "selectfolder");
  }

  private void pdfSplit10(ActionEvent e) {
    pdfSplitBySize(e, 10);
  }
  
  private void pdfSplitN(ActionEvent e) {
    pdfSplitBySize(e, 0);
  }

  private void pdfSplitCount1(ActionEvent e) {
    pdfSplitByCount(e, 1);
  }
  
  private void pdfSplitCountN(ActionEvent e) {
    pdfSplitByCount(e, 0);
  }

  private void pdfSplitByP(ActionEvent e) {
    pdfSplitByParity(e, true);
  }
  
  private void pdfSplitByI(ActionEvent e) {
    pdfSplitByParity(e, false);
  }
  
  private void sign(ActionEvent e, String sendTo) {
    signFile(pdfs, sendTo);
    signFile(mp4s, sendTo);
    close();
  }

  private void signFile(List<File> files, String sendTo) {
    forEach(files, f -> {
      ShellExtension.main(
          PjeTaskReader.CNJ_ASSINADOR.getId(), 
          f.getAbsolutePath(), 
          sendTo,  //enviarPara
          PjeSignMode.DEFINIDO.getKey(), //modo
          AssinaturaPadrao.NOT_ENVELOPED.getKey(),//padraoAssinatura
          SignatureType.ATTACHED.getKey(), // tipoAssinatura
          SignatureAlgorithm.SHA1withRSA.getName() //algoritmoHash
        ); 
    });
  }

  private void pdfSplitBySize(ActionEvent e, int size) {
    forEach(pdfs, f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_SIZE.getId(), 
        f.getAbsolutePath(),
        Integer.toString(size)
      );
    });
  }

  private void pdfSplitByCount(ActionEvent e, int count) {
    forEach(pdfs, f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_COUNT.getId(), 
        f.getAbsolutePath(),
        Integer.toString(count)
      );
    });
  }

  private void pdfSplitByParity(ActionEvent e, boolean parity) {
    forEach(pdfs, f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_PARITY.getId(), 
        f.getAbsolutePath(),
        Boolean.toString(parity)
      );
    });
  }
  
  private void pdfSplitByPages(ActionEvent e) {
    forEach(pdfs, f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_PAGES.getId(), 
        f.getAbsolutePath()
      );
    });
  }
  
  private void pdfJoin(ActionEvent e) {
    forEach(pdfs, f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_JOIN.getId(), 
        f.getAbsolutePath()
      );
    });
  }
  
  private void mp4SplitBySize(ActionEvent action, int size) {
    forEach(mp4s, f -> {
      ShellExtension.main(
        PjeTaskReader.VIDEO_SPLIT_BY_SIZE.getId(),
        f.getAbsolutePath(),
        Integer.toString(size)
      ); 
    });
  }

  private void mp4SplitByDuration(ActionEvent action, int duration) {
    forEach(mp4s, f -> {
      ShellExtension.main(
        PjeTaskReader.VIDEO_SPLIT_BY_DURATION.getId(),
        f.getAbsolutePath(),
        Integer.toString(duration)
      ); 
    });
  }
  
  private void mp4Slice(ActionEvent action) {
    forEach(mp4s, f -> {
      ShellExtension.main(
        PjeTaskReader.VIDEO_SPLIT_BY_SLICE.getId(),
        f.getAbsolutePath()        
      ); 
    });
  }
  
  private void mp4Audio(ActionEvent action) {
    forEach(mp4s, f -> {
      ShellExtension.main(
        PjeTaskReader.VIDEO_EXTRACT_AUDIO.getId(),
        f.getAbsolutePath()        
      ); 
    });
  }

  private void mp4Webm(ActionEvent action) {
    forEach(mp4s, f -> {
      ShellExtension.main(
        PjeTaskReader.VIDEO_CONVERT_WEBM.getId(),
        f.getAbsolutePath()        
      ); 
    });
  }  
  
  private void forEach(List<File> files, Consumer<File> consumer) {
    if (files.size() > 150) {
      startAsync(() -> {
        IProgressView progress = PjeProgressFactory.DEFAULT.get();
        try {
          progress.display();
          progress.begin(Stage.ANALYZING, files.size());
          for(File f : files) {
            progress.step("Adicionando arquivo '%s'", f.getName());
            consumer.accept(f);
          };
          progress.end();
        } catch (InterruptedException e) {
        }finally {
          progress.undisplay();
          progress.dispose();
        }
      });
    } else {
      files.forEach(consumer::accept);
    }
    close();
  }  

  @Override
  protected void onEscPressed(ActionEvent e) {
    setAlwaysOnTop(false);
    Boolean cancell = Dialogs.getBoolean(
      "Deseja mesmo cancelar a operação?",
      "Cancelamento da operação", 
      false
    );
    if (Booleans.isTrue(cancell, false)) {
      this.close();
    }
  }
}