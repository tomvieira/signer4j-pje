package br.jus.cnj.pje.office.core.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class PdfHandler implements IPdfHandler {

  public Observable<IPdfStatus> splitByCount(IInputDesc desc, long pgCount) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public Observable<IPdfStatus> splitByPages(IInputDesc desc, String... pages) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
  
  public Observable<IPdfStatus> join(IInputDesc desc) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }
  
  public Observable<IPdfStatus> splitBySize(final IInputDesc desc, final long maxSize) {
    return Observable.create(new ObservableOnSubscribe<IPdfStatus>() {
      @Override
      public void subscribe(ObservableEmitter<IPdfStatus> emitter) throws Exception {
        try {
          for(Path f: desc.getInputPdfs()) {
            final File file = f.toFile();
            emitter.onNext(new PdfStatus("Processando arquivo " + file.getName()));
            
            final PdfReader inputPdf = new PdfReader(file.getAbsolutePath());
            final int totalPages = inputPdf.getNumberOfPages();
            int pageNumber = 1;
            File fileOutput = null;
            try {
              if (totalPages <= 1) {
                fileOutput = desc.resolveOutput("" + pageNumber);
                try(OutputStream out = new FileOutputStream(fileOutput)) {
                  Files.copy(f, out);
                }
                emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
              } else {
                Document document = new Document();
                fileOutput = desc.resolveOutput("" + pageNumber);
                PdfCopy copy = new PdfCopy(document , new FileOutputStream(fileOutput));
                document.open();
                long combinedSize = 0;
                int beginPage = 1;
                do {
                  if (pageNumber > 1 && combinedSize == 0) {
                    document = new Document();
                    fileOutput = desc.resolveOutput("" + pageNumber);
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
            }catch(Throwable e) {
              if (fileOutput != null) {
                fileOutput.delete();
              }
            }
          };
          emitter.onComplete();
        }catch(Throwable e) {
          emitter.onError(e);
        }
      }
    });
  }
  
  public static void main(String[] args) {
    IPdfHandler handler = new PdfHandler();
    IInputDesc desc = new IInputDesc() {
      @Override
      public File resolveOutput(String fileName) {
        return getOutputFolder().resolve(getNamePrefix() + fileName + getNameSuffix() + ".pdf").toFile();
      }
      @Override
      public Path getOutputFolder() {
        return Paths.get("D:/temp");
      }
      @Override
      public String getNameSuffix() {
        return "";//"-sufix";
      }
      @Override
      public String getNamePrefix() {
        return "";//"prefix-";
      }
      @Override
      public Iterable<Path> getInputPdfs() {
        return Arrays.asList(Paths.get("D:/temp/1800MB.pdf"));
      }
    };
    handler.splitBySize(desc, 100 * 1024 * 1024).subscribe((s) -> {
      System.out.println(s.getMessage());
    });
  }
}
