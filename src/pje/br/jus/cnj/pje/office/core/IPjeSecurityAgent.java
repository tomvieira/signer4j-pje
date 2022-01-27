package br.jus.cnj.pje.office.core;

import br.jus.cnj.pje.office.task.IMainParams;

public interface IPjeSecurityAgent {
  
  String PARAM_NAME = IPjeSecurityAgent.class.getSimpleName() + ".instance";
  
  void refresh();
  
  boolean isPermitted(IMainParams params, StringBuilder whyNot);
}
