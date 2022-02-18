package br.jus.cnj.pje.office.core.pdf.imp;

import static java.lang.String.valueOf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.signer4j.imp.Args;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import br.jus.cnj.pje.office.core.pdf.IInputDesc;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;
import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;
import br.jus.cnj.pje.office.core.pdf.IPdfStatus;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public abstract class AbstractPdfHandler implements IPdfHandler {
  
  private final IPdfPageRange[] ranges;
  private int iterator = 0;

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
  public Observable<IPdfStatus> apply(IInputDesc desc) {

    final ObservableOnSubscribe<IPdfStatus> subscriber = (emitter) -> {
      File fileOutput = null;
      try {  
        for(Path f: desc.getInputPdfs()) {
          final File file = f.toFile();
          emitter.onNext(new PdfStatus("Processando arquivo " + file.getName()));
          
          final PdfReader inputPdf = new PdfReader(file.getAbsolutePath());
          final int totalPages = inputPdf.getNumberOfPages();
          int pageNumber = 1;
  
          if (totalPages <= 1) {
            fileOutput = desc.resolveOutput(valueOf(pageNumber));
            try(OutputStream out = new FileOutputStream(fileOutput)) {
              Files.copy(f, out);
            }
            emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
          } else {
            IPdfPageRange next = nextRange();
            while(next != null) {
              int start = next.startPage();
              int beginPage = pageNumber = start;
              Document document = new Document();
              fileOutput = desc.resolveOutput(valueOf(pageNumber));
              PdfCopy copy = new PdfCopy(document , new FileOutputStream(fileOutput));
              document.open();
              long combinedPages = combinedStart(next);
              do {
                if (pageNumber > start && combinedPages == 0) {
                  document = new Document();
                  fileOutput = desc.resolveOutput(valueOf(pageNumber));
                  copy = new PdfCopy(document , new FileOutputStream(fileOutput));
                  document.open();
                }
                copy.addPage(copy.getImportedPage(inputPdf, pageNumber));
                combinedPages = combinedIncrement(combinedPages, copy); 
                if (mustSplit(combinedPages, next) || pageNumber == totalPages) {
                  document.close();
                  copy.close();
                  combinedPages = 0;
                  String fileOutputName = "pg_" + (beginPage == pageNumber ? beginPage : beginPage + "_ate_" + pageNumber);
                  fileOutput.renameTo(fileOutput = desc.resolveOutput(fileOutputName));
                  beginPage = pageNumber + 1;
                  emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
                  if (breakOnSplit())
                    break;
                }
              }while(pageNumber++ < totalPages);
              next = nextRange();
            }
          }
        };
        emitter.onComplete();
      }catch(Throwable e) {
        if (fileOutput != null) {
          fileOutput.delete();
        }
        emitter.onError(e);
      }
    };
    
    return Observable.create(subscriber);
  }

  protected boolean breakOnSplit() {
    return false;
  }

  protected abstract boolean mustSplit(long currentCombined, IPdfPageRange range);
}
