package br.jus.cnj.pje.office.core;

public interface IExitable {
  default void exit() {
    exit(0);
  }
  
  void exit(long delay);
}
