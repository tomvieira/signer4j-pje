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

import static java.security.Security.addProvider;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.signer4j.imp.Config;

import br.jus.cnj.pje.office.core.IPjeConfigPersister;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.gui.PjeImages;
import br.jus.cnj.pje.office.provider.PJeProvider;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;

public class PjeConfig extends Config {
  
  static {
    addProvider(new PJeProvider());
  }
  
  private PjeConfig() {}
  
  public static void setup() {
    setup(PjeImages.PJE_ICON.asImage(), new PJeConfigPersister());
  }
  
  protected static IPjeConfigPersister persister() {
    return (IPjeConfigPersister)Config.config();
  }
  
  public static void loadServerAccess(Consumer<IPjeServerAccess> add) {
    persister().loadServerAccess(add);
  }

  public static Optional<String> authStrategy() {
    return persister().authStrategy();
  } 

  public static void save(IPjeServerAccess... access) {
    persister().save(access);
  }

  public static void save(IPjeAuthStrategy strategy) {
    persister().save(strategy);
  }
  
  public static void overwrite(IPjeServerAccess... access) {
    persister().overwrite(access);
  }

  public static void delete(IPjeServerAccess access) {
    persister().delete(access);
  }
}
