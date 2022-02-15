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
import br.jus.cnj.pje.office.core.pdf.IPdfStatus;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class SplitBySizePdfHandler extends AbstractPdfHandler {

  private final long maxSize;
  
  public SplitBySizePdfHandler(long maxSize) {
    this.maxSize = Args.requirePositive(maxSize, "maxSize is < 1");
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
            Document document = new Document();
            fileOutput = desc.resolveOutput(valueOf(pageNumber));
            PdfCopy copy = new PdfCopy(document , new FileOutputStream(fileOutput));
            document.open();
            long combinedSize = 0;
            int beginPage = 1;
            do {
              if (pageNumber > 1 && combinedSize == 0) {
                document = new Document();
                fileOutput = desc.resolveOutput(valueOf(pageNumber));
                copy = new PdfCopy(document , new FileOutputStream(fileOutput));
                document.open();
              }
              copy.addPage(copy.getImportedPage(inputPdf, pageNumber));
              combinedSize = copy.getCurrentDocumentSize();
              if (combinedSize > maxSize || pageNumber == totalPages) {
                document.close();
                copy.close();
                combinedSize = 0;
                fileOutput.renameTo(fileOutput = desc.resolveOutput("pg_" + beginPage + "_ate_" + pageNumber));
                beginPage = pageNumber + 1;
                emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
              }
            }while(pageNumber++ < totalPages);
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

}
