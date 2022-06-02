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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Services;
import com.sun.net.httpserver.Filter;

import br.jus.cnj.pje.office.core.IPjeRequestHandler;

@SuppressWarnings("restriction")
class PjeWebServerBuilder implements IPjeWebServerSetup{
  
  private int port = PjeWebServer.HTTP_PORT;
  
  private final List<Filter> filters = new ArrayList<>();

  private final List<IPjeRequestHandler> handlers = new ArrayList<>();

  private final ExecutorService executor = Executors.newFixedThreadPool(8);

  @Override
  public int getPort() {
    return port;
  }

  @Override
  public ExecutorService getExecutor() {
    return executor;
  }

  @Override
  public Filter[] getFilters() {
    return this.filters.toArray(new Filter[this.filters.size()]);
  }

  @Override
  public IPjeRequestHandler[] getHandlers() {
    return this.handlers.toArray(new IPjeRequestHandler[this.filters.size()]);
  }
  
  PjeWebServerBuilder usingPort(int port) {
    this.port = Args.requirePositive(port, "port can't must be positive");
    return this;
  }
  
  PjeWebServerBuilder usingFilter(Filter filter) {
    Args.requireNonNull(filter, "filter is null");
    this.filters.add(filter);
    return this;
  }
  
  PjeWebServerBuilder usingHandler(IPjeRequestHandler handler) {
    this.handlers.add(Args.requireNonNull(handler, "handler is null"));
    return this;
  }

  public void shutdown() {
    Services.shutdownNow(executor, 2);    
  }
}
