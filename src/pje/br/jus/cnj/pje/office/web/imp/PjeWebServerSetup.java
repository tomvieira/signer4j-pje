package br.jus.cnj.pje.office.web.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Services;
import com.sun.net.httpserver.Filter;

import br.jus.cnj.pje.office.web.IPjeRequestHandler;

@SuppressWarnings("restriction")
class PjeWebServerSetup implements IPjeWebServerSetup{
  
  private int port = 8800;
  
  private final List<Filter> filters = new ArrayList<>();

  private final List<IPjeRequestHandler> handlers = new ArrayList<>();

  private final ExecutorService executor = Executors.newFixedThreadPool(4);

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
  
  PjeWebServerSetup usingPort(int port) {
    this.port = Args.requirePositive(port, "port can't must be positive");
    return this;
  }
  
  PjeWebServerSetup usingFilter(Filter filter) {
    Args.requireNonNull(filter, "filter is null");
    this.filters.add(filter);
    return this;
  }
  
  PjeWebServerSetup usingHandler(IPjeRequestHandler handler) {
    this.handlers.add(Args.requireNonNull(handler, "handler is null"));
    return this;
  }

  public void shutdown() {
    Services.shutdownNow(executor, 2);    
  }
}
