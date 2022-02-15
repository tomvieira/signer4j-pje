package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.pdf.IInputDesc;
import br.jus.cnj.pje.office.core.pdf.IPdfStatus;
import io.reactivex.Observable;

public class SplitByCountPdfHandler extends AbstractPdfHandler {

  private final int pageCount;
  
  public SplitByCountPdfHandler(int pageCount) {
    this.pageCount = Args.requirePositive(pageCount, "pageCount is < 1");
  }
  
  @Override
  public Observable<IPdfStatus> apply(IInputDesc t) {
    return null;
  }
}
