package br.jus.cnj.pje.office.imp;

public class IconNotFoundException extends IllegalStateException {
  public IconNotFoundException() {
    super("Não foi possível encontrar um ícone para a Versão Systray");
  }
}
