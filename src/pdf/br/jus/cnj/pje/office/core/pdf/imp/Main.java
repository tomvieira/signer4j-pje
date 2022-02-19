package br.jus.cnj.pje.office.core.pdf.imp;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import br.jus.cnj.pje.office.core.pdf.IInputDesc;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;

public class Main {
  
  static int it = 0;
  public static void main(String[] args) {
    IPdfHandler[] handlers = new IPdfHandler[] {
        new SplitBySizePdfHandler(300 * 1024 * 1024),
        new SplitByCountPdfHandler(300),
        new SplitByPagesPdfHandler(
          new PageRange(1, 1),
          new PageRange(3, 6),
          new PageRange(10, 20),
          new PageRange(25, 25),
          new PageRange(100, 200),
          new PageRange(400, 600)
        )
    };
    
    String[] output = new String[] {
      "bysize",
      "bycount",
      "bypages"
    };
    
    IInputDesc desc = new IInputDesc() {
      @Override
      public File resolveOutput(String fileName) {
        return getOutputFolder().resolve(getNamePrefix() + fileName + getNameSuffix() + ".pdf").toFile();
      }
      @Override
      public Path getOutputFolder() {
        Path out = Paths.get("D:/temp/" + output[it]);
        out.toFile().mkdirs();
        return out;
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

    it = 0;
    for(IPdfHandler handler: handlers) {
      handler.apply(desc).subscribe((s) -> {
        System.out.println(s.getMessage());
      });
      it++;
    }
  }

}
