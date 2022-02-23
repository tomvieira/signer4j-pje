package br.jus.cnj.pje.office.core.pdf.imp;

import static java.lang.String.valueOf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Throwables;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import br.jus.cnj.pje.office.core.pdf.IInputDescriptor;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;
import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;
import br.jus.cnj.pje.office.core.pdf.IPdfStatus;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public abstract class AbstractPdfHandler implements IPdfHandler {
  
  private final IPdfPageRange[] ranges;
  private int iterator = 0;
  protected int pageNumber = 0;

  public AbstractPdfHandler() {
    this(new PageRange());
  }
  
  public AbstractPdfHandler(IPdfPageRange... ranges) {
    this.ranges = Args.requireNonEmpty(ranges, "pages is empty");
  }
  
  protected long combinedStart(IPdfPageRange range) {
    return 0;
  }
  
  protected long combinedIncrement(long currentCombined, PdfCopy copy) {
    return currentCombined + 1;
  }
  
  protected final IPdfPageRange nextRange() {
    if (iterator == ranges.length)
      return null;
    IPdfPageRange next = ranges[iterator++];
    while(next == null && iterator < ranges.length)
      next = ranges[iterator++];
    return next;
  }
  
  @Override
  public void reset() {
    iterator = 0;
  }
  
  @Override
  public Observable<IPdfStatus> apply(IInputDescriptor desc) {

    final ObservableOnSubscribe<IPdfStatus> subscriber = (emitter) -> {
      File fileOutput = null;
      try {  
        for(File file: desc.getInputPdfs()) {
          emitter.onNext(new PdfStatus("Processando arquivo " + file.getName(), 0));
          
          final PdfReader inputPdf = new PdfReader(file.getAbsolutePath());
          final int totalPages = inputPdf.getNumberOfPages();
          if (totalPages <= 1) {
            fileOutput = desc.resolveOutput("pg_" + valueOf(1));
            try(OutputStream out = new FileOutputStream(fileOutput)) {
              Files.copy(file.toPath(), out);
            }
            emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), 1, fileOutput));
          } else {
            long max = Integer.MIN_VALUE;
            IPdfPageRange next = nextRange();
            while(next != null) {
              int start, beginPage = pageNumber = start = next.startPage();
                  
              Document document = new Document();
              fileOutput = desc.resolveOutput(valueOf("pg_" + beginPage));
              PdfCopy copy = new PdfCopy(document , new FileOutputStream(fileOutput));
              try {
                document.open();
                long combinedPages = combinedStart(next);
                do {
                  if (pageNumber > start && combinedPages == 0) {
                    beginPage = pageNumber;
                    document = new Document();
                    fileOutput = desc.resolveOutput(valueOf(pageNumber));
                    copy = new PdfCopy(document , new FileOutputStream(fileOutput));
                    document.open();
                  }
                  long before = combinedPages;
                  copy.addPage(copy.getImportedPage(inputPdf, pageNumber));
                  combinedPages = combinedIncrement(combinedPages, copy);
                  max = Math.max(combinedPages - before, max);
                  if (mustSplit(combinedPages, next, max, totalPages) || isEnd(totalPages)) {
                    document.close();
                    copy.close();
                    combinedPages = 0;
                    String fileOutputName = computeFileName(beginPage);
                    File resolve = desc.resolveOutput(fileOutputName);
                    resolve.delete();
                    fileOutput.renameTo(resolve);
                    emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), pageNumber, fileOutput));
                    if (breakOnSplit())
                      break;
                  } else {
                    emitter.onNext(new PdfStatus("Adicionada página ", pageNumber));
                  }
                }while(hasNext(totalPages));
                next = nextRange();
              }catch(Throwable e) {
                Throwables.tryRun(document::close);
                Throwables.tryRun(copy::close);
                throw e;
              }
            }
          }
        };
        emitter.onComplete();
      }catch(Throwable e) {
        if (fileOutput != null) {
          fileOutput.delete();
        }
        emitter.onError(e);
      }finally {
        reset();
      }
    };
    
    return Observable.create(subscriber);
  }

  protected String computeFileName(int beginPage) {
    return "pg_" + (beginPage == pageNumber ? beginPage : beginPage + "_ate_" + pageNumber);
  }
  
  protected int getEndReference(int totalPages) {
    return totalPages;
  }

  protected final boolean isEnd(final int totalPages) {
    return pageNumber >= getEndReference(totalPages);
  }

  protected final boolean hasNext(final int totalPages) {
    return nextPage() < getEndReference(totalPages);
  }

  protected int nextPage() {
    return pageNumber++;
  }

  protected boolean breakOnSplit() {
    return false;
  }

  protected abstract boolean mustSplit(long currentCombined, IPdfPageRange range, long max, int totalPages);
}
