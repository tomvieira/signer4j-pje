package br.jus.cnj.pje.office.core;

public interface IPjeWebServer {
  
  int HTTP_PORT = 8800;
  
  int HTTPS_PORT = 8801; 

  String BASE_END_POINT = "/pjeOffice/";
  
  String SHUTDOWN_ENDPOINT = BASE_END_POINT + "shutdown"; 
  
  String LOGOUT_ENDPOINT = BASE_END_POINT + "logout";
}
