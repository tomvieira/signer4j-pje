package br.jus.cnj.pje.office;

public interface IBootable {

  String getOrigin();
  
  void boot();
  
  void logout();

  void exit(long delay);

  default void exit() {
    exit(0);
  }
}
