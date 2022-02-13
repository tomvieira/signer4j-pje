package br.jus.cnj.pje.office.core.imp;

import java.util.concurrent.ExecutorService;

import com.sun.net.httpserver.Filter;

import br.jus.cnj.pje.office.core.IPjeRequestHandler;

interface IPjeWebServerSetup {
  
  int getPort();

  ExecutorService getExecutor();
  
  Filter[] getFilters();
  
  IPjeRequestHandler[] getHandlers();
}
