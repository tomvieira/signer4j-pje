package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.SimpleContext.of;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

import com.github.signer4j.IFinishable;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Ids;
import com.github.signer4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeContext;

public class PjeClipServer extends PjeTextServer {

  private String lastclip = Ids.next();
  
  public PjeClipServer(IFinishable finishingCode) {
    super(finishingCode, "clip://global-messaging");
  }

  @Override
  protected IPjeContext createContext(String input) throws Exception {
    return of(new PjeClipRequest(input), new PjeClipResponse(Constants.DEFAULT_CHARSET));
  }
  
  @Override
  protected IPjeContext createContext() throws InterruptedException, Exception {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
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
      String input = Strings.trim((String)t.getTransferData(DataFlavor.stringFlavor));      
      if (!canProccess(input)) {
        LOGGER.info("Rejeitado: " + input); //TODO we have to go back here! change to DEBUG instead
        lastclip = input;
        continue;        
      }
      lastclip = input; 
      return createContext(input);
    }while(true);
  }

  private boolean canProccess(String input) {
    return !lastclip.equals(input) && input.startsWith(getServerEndpoint());
  }
}

