package br.jus.cnj.pje.office.core;

import java.io.IOException;

import com.github.utils4j.ILifeCycle;

import io.reactivex.Observable;

public interface IPJeLifeCycle extends ILifeCycle<IOException> {
  enum LifeCycle {
    STARTUP,
    SHUTDOWN,
    KILL
  }
  
  Observable<LifeCycle> lifeCycle();
}
