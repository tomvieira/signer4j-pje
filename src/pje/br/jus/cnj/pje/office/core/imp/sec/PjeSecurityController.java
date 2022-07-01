package br.jus.cnj.pje.office.core.imp.sec;

import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.task.IMainParams;

public enum PjeSecurityController implements IPjeSecurityAgent {

  ROOT(PjeSecurityAgent.SAFE); //default to SAFE
  
  
  private PjeSecurityController(IPjeSecurityAgent agent) {
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
  
  public IPjeSecurityAgent safe() {
    agent = PjeSecurityAgent.SAFE;
    return this;
  }
  
  public IPjeSecurityAgent unsafe() {
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
