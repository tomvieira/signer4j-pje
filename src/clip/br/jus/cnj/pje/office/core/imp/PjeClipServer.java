package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.UTIL_DOWNLOADER;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

import com.github.utils4j.imp.Ids;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeClipServer extends PjeURIServer {
  
  private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  
  private String lastUri = Ids.next();
  
  public PjeClipServer(IBootable boot) {
    super(boot, "clip://global-messaging");
  }
  
  protected boolean isLast(String uri) {
    return lastUri.equals(uri);
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
      String stringContent = Strings.trim((String)t.getTransferData(DataFlavor.stringFlavor));
      
      Optional<String> uri = nextUri(stringContent);
      
      if (!uri.isPresent()) {
        continue;
      }

      return getServerEndpoint(uri.get());
    }while(true);
  }

  private Optional<String> nextUri(String content) {
    if (content.isEmpty() || isLast(content)) {
      return Optional.empty();
    }

    lastUri = content;

    if (!lastUri.startsWith("https://jsoncompare.org")) {
      return Optional.empty();
    }
    
    final Params params = Params.create()
        .of("servidor", getServerEndpoint())
        .of("url", content)
        .of("enviarPara", "C:\\Users\\Leonardo\\Documents\\TEMP\\baixado.mp4");

    Optional<String> uri;
    try {
      uri = Optional.ofNullable(UTIL_DOWNLOADER.toUri(params));
    } catch (Exception e) {
      uri = Optional.empty();
    }
    
    return uri;
  }
}

