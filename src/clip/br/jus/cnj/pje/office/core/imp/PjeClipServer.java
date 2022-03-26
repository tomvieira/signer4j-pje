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

    if (!lastUri.startsWith("https://jsoncompare.org")) { //WE HAVE TO GO BACK HERE! KEEP CALM!
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

