package br.jus.cnj.pje.office.core;

public interface ISecurityAgent {
  
  String PJE_SECURITY_AGENT_PARAM = ISecurityAgent.class.getSimpleName() + ".instance";
  
  void refresh();
  
  boolean isPermitted(IPjeMainParams params, StringBuilder whyNot);
}
