package br.jus.cnj.pje.office.core.pdf.imp;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import br.jus.cnj.pje.office.core.pdf.IInputDesc;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;

public class Main {
  
  public static void main(String[] args) {
    IPdfHandler[] handlers = new IPdfHandler[] {
        new SplitBySizePdfHandler(10 * 1024 * 1024),
        new SplitByCountPdfHandler(4),
        new SplitByPagesPdfHandler(
          new PageRange(1, 1),
          new PageRange(3, 6),
          new PageRange(10, 20),
          new PageRange(25, 25)
        )
    };
    
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
        return Arrays.asList(Paths.get("D:/temp/60MB.pdf"));
      }
    };
  
    for(IPdfHandler handler: handlers) {
      handler.apply(desc).subscribe((s) -> {
        System.out.println(s.getMessage());
      });
      System.out.println("ANALISA E APAGA");
    }
  }

}
