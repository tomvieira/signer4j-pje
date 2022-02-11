package br.jus.cnj.pje.office.core;

import java.io.IOException;

import io.reactivex.Observable;

public interface IPjeCommander<I extends IPjeRequest, O extends IPjeResponse>  {
  
  enum LifeCycle {
    STARTUP,
    SHUTDOWN,
    KILL
  }
  
  void start() throws IOException;

  void stop(boolean force) throws IOException;

  boolean isStarted();

  Observable<LifeCycle> lifeCycle();

  void exit();

  void showOfflineSigner();
  
  void execute(I request, O response);

  void logout();

}