package br.jus.cnj.pje.office.core;

public interface IPjeLifeCycleHook {
  default void onShutdown() {}
  
  default void onStartup() {}
  
  default void onKill() {}

  default void onFailStart(Exception e) {}
  
  default void onFailShutdown(Exception e) {}
}
