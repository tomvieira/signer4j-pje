package br.jus.cnj.pje.office.core;

public interface IPjeCommander<I extends IPjeRequest, O extends IPjeResponse> extends IPJeLifeCycle {
  
  void execute(String uri);
  
  void execute(I request, O response);

  String getServerEndpoint();

}