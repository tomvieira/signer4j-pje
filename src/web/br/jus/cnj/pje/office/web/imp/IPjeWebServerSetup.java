package br.jus.cnj.pje.office.web.imp;

import java.util.concurrent.ExecutorService;

import com.sun.net.httpserver.Filter;

import br.jus.cnj.pje.office.web.IPjeRequestHandler;

interface IPjeWebServerSetup {
  
  int getPort();

  ExecutorService getExecutor();
  
  Filter[] getFilters();
  
  IPjeRequestHandler[] getHandlers();
}
