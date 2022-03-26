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

import static com.github.utils4j.imp.Strings.trim;
import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.io.InterruptedIOException;

import com.github.utils4j.ILifeCycle;
import com.github.utils4j.imp.ThreadContext;
import com.github.utils4j.imp.Threads;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeURIServer extends DefaultPjeCommander {

  private final ILifeCycle<IOException> capturer;

  public PjeURIServer(IBootable boot, String serverAddress) {
    super(boot, serverAddress);
    this.capturer = new URICapturer(serverAddress);
  }

  @Override
  protected void doStart() throws IOException {
    this.capturer.start();
    super.doStart();
  }

  @Override
  protected void doStop(boolean kill) throws IOException {
    this.capturer.stop(2000);
    super.doStop(kill);
  }

  protected void submit(IPjeContext context) {
    if (context != null) {
      super.execute(context.getRequest(), context.getResponse());
    }
  }

  protected void clearBuffer() {}

  @Override
  protected final void openRequest(String request) {
    tryRun(() -> submit(createContext(getServerEndpoint("/") + request)));
  }

  private class URICapturer extends ThreadContext<IOException> {

    public URICapturer(String contextName) {
      super(contextName);
    }

    @Override
    protected void beforeRun() {
      clearBuffer();
    }

    protected boolean isValid(String uri) {
      return uri.startsWith(getServerEndpoint());
    }

    private final IPjeContext createContext() throws Exception {
      String uri = trim(getUri());
      if (!isValid(uri)) {
        return null;
      }
      return PjeURIServer.this.createContext(uri);
    }

    @Override
    protected void doRun() throws Exception {
      int errorCount = 0;
      do {
        final IPjeContext context;
        try {
          context = createContext();
          errorCount = 0;
        } catch (InterruptedException | InterruptedIOException e) {
          Thread.currentThread().interrupt();
          LOGGER.warn("Thread interrompida", e);
          break;
        } catch (Exception e) {
          LOGGER.warn("Requisição mal formada", e);
          if (++errorCount > 10) {
            LOGGER.error("Total máximo de erros alcançado: {}. Thread finalizada", errorCount);
            break;
          }
          if (!Thread.interrupted()) {
            clearBuffer();
            continue;
          }
          LOGGER.warn("Thread interrompida", e);
          break;
        }
        if (context == null) {
          LOGGER.warn("Contexto indisponível");
          continue;
        }
        Threads.startAsync("Tratando contexto: " + context.getId(), () -> submit(context));
      }while(true);
    }
  }
  
  private final IPjeContext createContext(String uri) throws Exception {
    return SimpleContext.of(createRequest(uri, boot.getOrigin()), createResponse());
  }

  protected abstract IPjeResponse createResponse() throws Exception;

  protected abstract IPjeRequest createRequest(String uri, String origin) throws Exception;
  
  protected abstract String getUri() throws InterruptedException, Exception;

}
