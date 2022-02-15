package br.jus.cnj.pje.office.core.pdf.imp;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import br.jus.cnj.pje.office.core.pdf.IInputDesc;
import br.jus.cnj.pje.office.core.pdf.IPdfHandler;

public class Main {
  
  public static void main(String[] args) {
    IPdfHandler handler = new SplitBySizePdfHandler(100 * 1024 * 1024);
    
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
    handler.apply(desc).subscribe((s) -> {
      System.out.println(s.getMessage());
    });
  }

}
