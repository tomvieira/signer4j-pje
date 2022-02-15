
package br.jus.cnj.pje.office.imp;  

import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.signer4j.imp.Throwables.tryRuntime;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Containers;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.imp.Threads;
import com.github.signer4j.imp.Threads.ShutdownHookThread;
import com.github.signer4j.imp.Throwables;

import br.jus.cnj.pje.office.core.IPjeLifeCycleHook;
import br.jus.cnj.pje.office.core.IPjeOffice;
import br.jus.cnj.pje.office.core.imp.PJeOffice;
import br.jus.cnj.pje.office.core.imp.PjeCommandFactory;
import br.jus.cnj.pje.office.core.imp.PjeConfig;

public abstract class PjeOfficeApp implements IPjeLifeCycleHook {
  
  static {
    PjeConfig.setup();
    tryRuntime(() -> setLookAndFeel(getSystemLookAndFeelClassName()));
  }

  protected static final Logger LOGGER = LoggerFactory.getLogger(PjeOfficeApp.class);

  protected IPjeOffice office;
  
  private ShutdownHookThread jvmHook;
  
  protected PjeOfficeApp(PjeCommandFactory factory, String... args) {
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
    display("Uma versão antiga do PjeOffice precisa ser fechada e/ou desinstalada do seu computador.\n" + e.getMessage());
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
