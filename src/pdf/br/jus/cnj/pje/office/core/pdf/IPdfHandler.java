package br.jus.cnj.pje.office.core.pdf;

import java.io.IOException;

import io.reactivex.Observable;

public interface IPdfHandler {
  
  Observable<IPdfStatus> splitBySize(IInputDesc desc, long maxSize);

  Observable<IPdfStatus> splitByCount(IInputDesc desc, long pgCount);

  Observable<IPdfStatus> splitByPages(IInputDesc desc, String... pages);
  
  Observable<IPdfStatus> join(IInputDesc desc) throws IOException;
}
