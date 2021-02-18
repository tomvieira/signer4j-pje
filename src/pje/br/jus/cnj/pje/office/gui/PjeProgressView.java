package br.jus.cnj.pje.office.gui;

import java.util.function.Supplier;

import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.imp.ProgressFactory;

import br.jus.cnj.pje.office.core.IPjeProgressView;

public enum PjeProgressView implements IPjeProgressView, Supplier<IProgressFactory> {
  INSTANCE;

  @Override
  public void display() {
    this.view.display();
  }

  @Override
  public void undisplay() {
    this.view.undisplay();
  }

  @Override
  public IProgressFactory get() {
    return view.getProgressFactory();
  }
  
  private final PjeProgressWindow view = new PjeProgressWindow(ProgressFactory.DEFAULT.get());
}
