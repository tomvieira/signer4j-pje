package br.jus.cnj.pje.office.core;

public interface IPjeSecurityAgent {
  
  String PJE_SECURITY_AGENT_PARAM = IPjeSecurityAgent.class.getSimpleName() + ".instance";
  
  void refresh();
  
  boolean isPermitted(IPjeMainParams params, StringBuilder whyNot);
}
