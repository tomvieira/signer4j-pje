package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.SimpleContext.of;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeContext;

class PjeClipServer extends PjeURIServer {
  
  private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  
  public PjeClipServer(IBootable boot) {
    super(boot, "clip://global-messaging");
  }
  
  @Override
  protected void clearBuffer() {
    clipboard.setContents(new StringSelection(""), null);
  }

  @Override
  protected IPjeContext createContext(String input) throws Exception {
    return of(new PjeClipRequest(input, boot.getOrigin()), new PjeClipResponse());
  }
  
  @Override
  protected String getUri() throws InterruptedException, Exception {
    do {
      Thread.sleep(1000);
      Optional<Transferable> content = Optional.ofNullable(clipboard.getContents(this));      
      if (!content.isPresent()) {
        continue;
      }
      Transferable t = content.get();
      if (!t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        continue;
      }
      String uri = Strings.trim((String)t.getTransferData(DataFlavor.stringFlavor));
      return uri;
    }while(true);
  }
}

