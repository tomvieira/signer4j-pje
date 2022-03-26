/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PjeAccessTime.fromOptions;

import java.util.Scanner;

import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjePermissionAccessor;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.gui.alert.PjePermissionAccessor;

enum PjePermissionAcessor implements IPjePermissionAccessor {
  PRODUCTION(){
    private final IPjePermissionAccessor acessor = new PjePermissionAccessor();
    
    @Override
    public PjeAccessTime tryAccess(IPjeServerAccess token) {
      return acessor.tryAccess(token);
    }
  },
  CONSOLE(){
    @Override
    public PjeAccessTime tryAccess(IPjeServerAccess token) {
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
