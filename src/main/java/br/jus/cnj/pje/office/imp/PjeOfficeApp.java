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

package br.jus.cnj.pje.office.imp;  

import static br.jus.cnj.pje.office.core.imp.PjeConfig.setup;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.gui.alert.MessageAlert;
import com.github.utils4j.gui.imp.LookAndFeelsInstaller;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.Environment;
import com.github.utils4j.imp.Strings;
import com.github.utils4j.imp.Threads;
import com.github.utils4j.imp.Threads.ShutdownHookThread;
import com.github.utils4j.imp.Throwables;

import br.jus.cnj.pje.office.core.IPjeLifeCycleHook;
import br.jus.cnj.pje.office.core.IPjeOffice;
import br.jus.cnj.pje.office.core.imp.PJeOffice;
import br.jus.cnj.pje.office.core.imp.PjeLifeCycleFactory;

public abstract class PjeOfficeApp implements IPjeLifeCycleHook {

  static final String ENVIRONMENT_VARIABLE_NAME = "PJEOFFICE_LOOKSANDFEELS";
  
  static {
    install();
  }
  
  private static void install() {
    setup();
    LookAndFeelsInstaller.install(Environment.valueFrom(ENVIRONMENT_VARIABLE_NAME).orElse("undefined"));
  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(PjeOfficeApp.class);

  protected IPjeOffice office;
  
  private ShutdownHookThread jvmHook;
  
  protected PjeOfficeApp(PjeLifeCycleFactory factory, String... args) {
    this.office = new PJeOffice(this, factory, originFrom(args));
    this.jvmHook = Threads.shutdownHookAdd(office::exit, "JVMShutDownHook");
  }

  @Override
  public void onKill() {
    LOGGER.info("Reciclando jvmHook");
    Threads.shutdownHookRem(jvmHook);
    this.jvmHook = null;
    LOGGER.info("App closed");
    this.office = null;
  }

  @Override
  public final void onFailStart(Exception e) {
    MessageAlert.showInfo("Uma versão antiga do PjeOffice precisa ser fechada e/ou desinstalada do seu computador.\n" + e.getMessage());
    System.exit(1);
  }

  protected void start() {
    office.boot();
  }
  
  private static String originFrom(String ...args) {
    if (Containers.isEmpty(args))
      return Strings.empty();
    String uri = args[0];
    return Throwables.tryCall(() -> new URI(uri).toString(), Strings.empty());
  }  
}
