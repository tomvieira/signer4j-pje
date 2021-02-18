package br.jus.cnj.pje.office.web;

import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public interface IPjeRequestHandler extends HttpHandler {
  public String getEndPoint();
}
