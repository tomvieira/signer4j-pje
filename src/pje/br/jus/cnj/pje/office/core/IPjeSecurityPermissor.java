package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.task.IMainParams;

public interface IPjeSecurityPermissor {

  boolean isPermitted(IMainParams params, StringBuilder whyNot);

}