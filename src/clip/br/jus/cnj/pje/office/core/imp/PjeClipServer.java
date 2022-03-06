package br.jus.cnj.pje.office.core.imp;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

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
  protected IPjeResponse createResponse() throws Exception {
    return new PjeClipResponse();
  }

  @Override
  protected IPjeRequest createRequest(String input, String origin) throws Exception{
    return new PjeClipRequest(input, origin);
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

