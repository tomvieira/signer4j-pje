package br.jus.cnj.pje.office.core.pdf.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import br.jus.cnj.pje.office.core.pdf.IInputDescriptor;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;

public class Main {
  
  public static void main(String[] args) throws IOException {
    IPdfHandler[] handlers = new IPdfHandler[] {
      new SplitBySizePdfHandler((int)(10 * 1024 * 1024)),
      new SplitByCountPdfHandler(300),
      new SplitByPagesPdfHandler(
        new PageRange(1, 1),
        new PageRange(3, 6),
        new PageRange(10, 20),
        new PageRange(25, 25),
        new PageRange(100, 200),
        new PageRange(400, 600)
      ),
      new SplitByOddPagesPdfHandler(),
      new SplitByEvenPagesPdfHandler(),
      new SplitBySinglePagePdfHandler()
    };
    
    final String[] outputPath = new String[] {
      "bysize",
      "bycount",
      "bypages",
      "byodd",
      "byeven",
      "bysingle"
    };
    
    Path baseInput = Paths.get("D:/temp/");
    int i = 0;
    for(IPdfHandler handler: handlers) {
      IInputDescriptor desc = new InputDescriptor.Builder()
        .add(baseInput.resolve("1200MB.pdf").toFile())
        .output(baseInput.resolve(outputPath[i++]))
        .build();
      handler.apply(desc).subscribe((s) -> {
        System.out.println(s.toString());
      });
    }
  }

}
