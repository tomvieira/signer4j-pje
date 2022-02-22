package br.jus.cnj.pje.office.core;

public interface IPjeContext {
  String getId();
  IPjeRequest getRequest();
  IPjeResponse getResponse();
}
