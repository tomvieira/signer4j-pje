/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.util.List;

import com.github.utils4j.imp.Containers;

import br.jus.cnj.pje.office.core.IPJeLifeCycle;
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
            error.addSuppressed(e);
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
