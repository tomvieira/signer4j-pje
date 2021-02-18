package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.fromOptions;

import java.util.Scanner;

import com.github.signer4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IServerAccess;
import br.jus.cnj.pje.office.gui.alert.PjePermissionAccessor;

public enum PjePermissionAcessor implements IPjePermissionAccessor {
  PRODUCTION(){
    private final IPjePermissionAccessor acessor = new PjePermissionAccessor();
    
    @Override
    public PjeAccessTime tryAccess(IServerAccess token) {
      return acessor.tryAccess(token);
    }
  },
  CONSOLE(){
    @Override
    public PjeAccessTime tryAccess(IServerAccess token) {
      @SuppressWarnings("resource")
      Scanner sc = new Scanner(System.in);
      do{
        System.out.println("==============================");
        System.out.println("= Autorização de Servidor:    ");
        System.out.println("==============================");

        PjeAccessTime.printOptions(System.out);
        
        System.out.print("Option : ");  
        String option = Strings.trim(sc.nextLine());
        
        PjeAccessTime choosed = fromOptions(Strings.toInt(option, -1)-1);
        
        if (choosed == null) {
          continue;
        }
        
        return choosed;
        
      } while(true);
    }
  };
}
