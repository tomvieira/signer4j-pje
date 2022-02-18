package br.jus.cnj.pje.office.core.pdf.imp;

import com.github.signer4j.imp.Args;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class SplitByCountPdfHandler extends AbstractPdfHandler {

  private final int pageCount;
  
  public SplitByCountPdfHandler(int pageCount) {
    super(new PageRange());
    this.pageCount = Args.requirePositive(pageCount, "pageCount is < 1");
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPdfPageRange range) {
    return currentCombined >= pageCount;
  }

//  @Override
//  public Observable<IPdfStatus> apply(IInputDesc desc) {
//    
//    final ObservableOnSubscribe<IPdfStatus> subscriber = (emitter) -> {
//      File fileOutput = null;
//      try {  
//        for(Path f: desc.getInputPdfs()) {
//          final File file = f.toFile();
//          emitter.onNext(new PdfStatus("Processando arquivo " + file.getName()));
//          
//          final PdfReader inputPdf = new PdfReader(file.getAbsolutePath());
//          final int totalPages = inputPdf.getNumberOfPages();
//          int pageNumber = 1;
//  
//          if (totalPages <= 1) {
//            fileOutput = desc.resolveOutput(valueOf(pageNumber));
//            try(OutputStream out = new FileOutputStream(fileOutput)) {
//              Files.copy(f, out);
//            }
//            emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
//          } else {
//            Document document = new Document();
//            fileOutput = desc.resolveOutput(valueOf(pageNumber));
//            PdfCopy copy = new PdfCopy(document , new FileOutputStream(fileOutput));
//            document.open();
//            long combinedPages = 0;
//            int beginPage = 1;
//            do {
//              if (pageNumber > 1 && combinedPages == 0) {
//                document = new Document();
//                fileOutput = desc.resolveOutput(valueOf(pageNumber));
//                copy = new PdfCopy(document , new FileOutputStream(fileOutput));
//                document.open();
//              }
//              copy.addPage(copy.getImportedPage(inputPdf, pageNumber));
//              combinedPages += 1; 
//              if (combinedPages >= pageCount || pageNumber == totalPages) {
//                document.close();
//                copy.close();
//                combinedPages = 0;
//                String fileOutputName = "pg_" + (beginPage == pageNumber ? beginPage : beginPage + "_ate_" + pageNumber);
//                fileOutput.renameTo(fileOutput = desc.resolveOutput(fileOutputName));
//                beginPage = pageNumber + 1;
//                emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
//              }
//            }while(pageNumber++ < totalPages);
//          }
//        };
//        emitter.onComplete();
//      }catch(Throwable e) {
//        if (fileOutput != null) {
//          fileOutput.delete();
//        }
//        emitter.onError(e);
//      }
//    };
//    
//    return Observable.create(subscriber);
//  }
}
