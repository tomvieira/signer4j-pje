package br.jus.cnj.pje.office.web.imp;

import java.util.concurrent.ExecutorService;

import com.sun.net.httpserver.Filter;

import br.jus.cnj.pje.office.web.IPjeRequestHandler;

@SuppressWarnings("restriction")
interface IPjeWebServerSetup {
  
  int getPort();

  ExecutorService getExecutor();
  
  Filter[] getFilters();
  
  IPjeRequestHandler[] getHandlers();
}
