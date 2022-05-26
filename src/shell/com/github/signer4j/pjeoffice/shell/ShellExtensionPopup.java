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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.imp.SignatureType;
import com.github.utils4j.IOfferer;
import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.gui.imp.SimpleFrame;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Booleans;

import br.jus.cnj.pje.office.task.imp.AssinaturaPadrao;
import br.jus.cnj.pje.office.task.imp.PjeSignMode;
import br.jus.cnj.pje.office.task.imp.PjeTaskReader;
import net.miginfocom.swing.MigLayout;

public class ShellExtensionPopup extends SimpleFrame {
  private static final long serialVersionUID = 1L;
  
  private static class ActionButton extends JButton {
    private static final long serialVersionUID = 1L;

    ActionButton(String label, ActionListener action) {
      super(label);
      setHorizontalAlignment(SwingConstants.LEFT);
      addActionListener(action);
    }
  }
    
  private final IOfferer offerer;
  private final List<File> files;
  
  public ShellExtensionPopup(IOfferer offerer, List<File> files) {
    super("Ações");
    this.offerer = Args.requireNonNull(offerer, "offerer is null");
    this.files = Args.requireNonEmpty(files, "files is empty");
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
    createSigningActions();
    createPdfActions();
//    createMp4Actions();
  }

  private void createSigningActions() {
    addAction("Assinar e salvar na mesma pasta", this::signAtSameFolder);
    addAction("Assinar e salvar em nova pasta", this::signAtNewFolder);
    addAction("Assinar e salvar em pasta específica...", this::signAtOtherFolder);
  }
    
  private void createPdfActions() {
    addAction("Gerar 1 pdf a cada 10MB (Pje)", this::split10);
    addAction("Gerar 1 pdf a cada 'n'MB...", this::splitN);
    addAction("Gerar 1 pdf por página", this::splitCount1);
    addAction("Gerar 1 pdf a cada 'n' páginas", this::splitCountN);
    addAction("Gerar 1 pdf com as páginas ÍMPARES", this::splitByI);
    addAction("Gerar 1 pdf com as páginas PARES", this::splitByP);
    addAction("Gerar pdf's com páginas específicas...", this::splitByPages);
    addAction("Unir pdf's selecionados", this::pdfJoin);
  }

//private void createMp4Actions() {
//  addAction("Gerar 1 vídeo a cada 90MB (Pje)");
//  addAction("Gerar 1 vídeo a cada 'n'MB...");
//  addAction("Gerar 1 vídeo a cada 10 minutos");
//  addAction("Gerar 1 vídeo a cada 15 minutos");
//  addAction("Gerar 1 vídeo a cada 20 minutos");
//  addAction("Gerar 1 vídeo a cada 25 minutos");
//  addAction("Gerar 1 vídeo a cada 30 minutos");
//  addAction("Gerar 1 vídeo a cada 1 hora");
//  addAction("Gerar 1 vídeo a cada 2 horas");
//  addAction("Gerar cortes específicos...");
//}

  private void signAtSameFolder(ActionEvent e) {
    sign(e, "samefolder");
  }

  private void signAtNewFolder(ActionEvent e) {
    sign(e, "newfolder");  
  }
  
  private void signAtOtherFolder(ActionEvent e) {
    sign(e, "selectfolder");
  }

  private void split10(ActionEvent e) {
    splitBySize(e, 10);
  }
  
  private void splitN(ActionEvent e) {
    splitBySize(e, 0);
  }

  private void splitCount1(ActionEvent e) {
    splitByCount(e, 1);
  }
  
  private void splitCountN(ActionEvent e) {
    splitByCount(e, 0);
  }

  private void splitByP(ActionEvent e) {
    splitByParity(e, true);
  }
  
  private void splitByI(ActionEvent e) {
    splitByParity(e, false);
  }
  
  private void sign(ActionEvent e, String sendTo) {
    files.forEach(f -> {
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
    close();
  }

  private void splitBySize(ActionEvent e, int size) {
    files.forEach(f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_SIZE.getId(), 
        f.getAbsolutePath(),
        Integer.toString(size)
      );
    });
    close();
  }

  private void splitByCount(ActionEvent e, int count) {
    files.forEach(f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_COUNT.getId(), 
        f.getAbsolutePath(),
        Integer.toString(count)
      );
    });
    close();
  }

  private void splitByParity(ActionEvent e, boolean parity) {
    files.forEach(f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_PARITY.getId(), 
        f.getAbsolutePath(),
        Boolean.toString(parity)
      );
    });
    close();
  }
  
  private void splitByPages(ActionEvent e) {
    files.forEach(f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_SPLIT_BY_PAGES.getId(), 
        f.getAbsolutePath()
      );
    });
    close();
  }
  
  private void pdfJoin(ActionEvent e) {
    files.forEach(f -> {
      ShellExtension.main(
        PjeTaskReader.PDF_JOIN.getId(), 
        f.getAbsolutePath()
      );
    });
    close();
  }
  
  private void addAction(String label, ActionListener action) {
    add(new ActionButton(label, action), "growx");
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
