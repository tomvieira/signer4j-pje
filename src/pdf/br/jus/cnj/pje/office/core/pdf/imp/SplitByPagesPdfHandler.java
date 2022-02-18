package br.jus.cnj.pje.office.core.pdf.imp;

import br.jus.cnj.pje.office.core.pdf.IPdfPageRange;

public class SplitByPagesPdfHandler extends AbstractPdfHandler{

  public SplitByPagesPdfHandler(IPdfPageRange... ranges) {
    super(ranges);
  }

  @Override
  public long combinedStart(IPdfPageRange range) {
    return range.startPage();
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPdfPageRange range) {
    return currentCombined >= range.endPage();
  };
  
  @Override
  protected boolean breakOnSplit() {
    return true;
  }

  /*
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
              long combinedPages = start; //mas será zero se for split por size ou split por total de paginas
              do {
                if (pageNumber > start && combinedPages == 0) {
                  document = new Document();
                  fileOutput = desc.resolveOutput(valueOf(pageNumber));
                  copy = new PdfCopy(document , new FileOutputStream(fileOutput));
                  document.open();
                }
                copy.addPage(copy.getImportedPage(inputPdf, pageNumber));
                combinedPages += 1; 
                if (combinedPages >= next.endPage() || pageNumber == totalPages) {
                  document.close();
                  copy.close();
                  combinedPages = 0;
                  String fileOutputName = "pg_" + (beginPage == pageNumber ? beginPage : beginPage + "_ate_" + pageNumber);
                  fileOutput.renameTo(fileOutput = desc.resolveOutput(fileOutputName));
                  beginPage = pageNumber + 1;
                  emitter.onNext(new PdfStatus("Gerado arquivo " + fileOutput.getName(), fileOutput));
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
  */
}
