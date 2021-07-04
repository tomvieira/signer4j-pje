package br.jus.cnj.pje.office.core.imp.sec;

import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeSecurityPermissor;
import br.jus.cnj.pje.office.task.IMainParams;

public enum PjeSecurity implements IPjeSecurityAgent {

  CONTROLLER(PjeSecurityAgent.SAFE); //default to SAFE
  
  
  private PjeSecurity(IPjeSecurityAgent agent) {
    this.agent = agent;
  }
  
  private volatile IPjeSecurityAgent agent;

  @Override
  public void refresh() {
    agent.refresh();
  }

  @Override
  public boolean isPermitted(IMainParams params, StringBuilder whyNot) {
    return agent.isPermitted(params, whyNot);
  }
  
  @Override
  public void setDevMode() {
    agent.setDevMode();
  }

  @Override
  public void setProductionMode() {
    agent.setProductionMode();
  }
  
  public IPjeSecurityPermissor safe() {
    agent = PjeSecurityAgent.SAFE;
    return this;
  }
  
  public IPjeSecurityPermissor unsafe() {
    agent = PjeSecurityAgent.UNSAFE;
    return this;
  }

  public boolean isUnsafe() {
    return agent == PjeSecurityAgent.UNSAFE;
  }

  public boolean isDevMode() {
    return agent.isDevMode();
  }
}
