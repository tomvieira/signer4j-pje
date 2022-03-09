package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.util.List;

import com.github.utils4j.imp.Containers;

import br.jus.cnj.pje.office.core.IPJeLifeCycle;
import br.jus.cnj.pje.office.core.IPjeCommander;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

class PJeCompositeLifeCycle implements IPJeLifeCycle {

  private final List<IPJeLifeCycle> cycles;
  
  private boolean started = false;
  
  private final BehaviorSubject<LifeCycle> startup = BehaviorSubject.create();
  
  PJeCompositeLifeCycle(IPJeLifeCycle... cycles) {
    this.cycles = Containers.arrayList(cycles);
  }
  
  @Override
  public Observable<LifeCycle> lifeCycle() {
    return startup;
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public void start() throws IOException {
    if (!isStarted()) {
      doStart();
      started = true;
      startup.onNext(LifeCycle.STARTUP);
    }
  }

  private void doStart() throws IOException {
    for(int i = 0; i < this.cycles.size(); i++) {
      IPJeLifeCycle cycle = this.cycles.get(i);
      if (cycle != null) {
        try {
          cycle.start();
        } catch (IOException e) {
          tryRun(() -> this.doStop(false));
          throw e;
        }
      }
    }
  }
  
  @Override
  public final void stop(boolean kill) throws IOException {
    if (isStarted()) {
      this.doStop(kill);
      this.started = false;
      startup.onNext(LifeCycle.SHUTDOWN);
      if (kill) {
        startup.onNext(LifeCycle.KILL);
      }
    }
  }

  private void doStop(boolean kill) throws IOException {
    IOException error = null;
    for(int i = 0; i < this.cycles.size(); i++) {
      IPJeLifeCycle cycle = this.cycles.get(i);
      if (cycle != null) {
        try {
          cycle.stop(kill);
        } catch (IOException e) {
          if (error == null) {
            error = e;
          }else {
            error = new IOException(error);
          }
        }
      }
    }
    if (error != null) {
      throw error;
    }
  }

  @Override
  public final void stop() throws IOException {
    stop(false);
  }
}
