package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.pdf.IInputDesc;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;
import br.jus.cnj.pje.office.core.pdf.IPdfStatus;
import io.reactivex.Observable;

public class SplitByPagesPdfHandler implements IPdfHandler {

  private final String[] pages;
  
  public SplitByPagesPdfHandler(String... pages) {
    this.pages = Args.requireNonEmpty(pages, "pages is empty");
  }
  
  @Override
  public Observable<IPdfStatus> apply(IInputDesc t) {
    // TODO Auto-generated method stub
    return null;
  }
}
