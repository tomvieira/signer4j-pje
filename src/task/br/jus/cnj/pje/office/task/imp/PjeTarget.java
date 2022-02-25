package br.jus.cnj.pje.office.task.imp;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.task.IPjeTarget;

class PjeTarget implements IPjeTarget {

  private final String endPoint;
  private final String userAgent;
  private final String session;

  PjeTarget(String endPoint, String userAgent, String session) {
    this.endPoint = Args.requireText(endPoint, "endpoint is null");
    this.userAgent = Args.requireNonNull(userAgent, "userAgent is null");
    this.session = Args.requireText(session, "session is null");//single sign on has empty string session but not null
  }
  
  @Override
  public String getEndPoint() {
    return endPoint;
  }

  @Override
  public String getUserAgent() {
    return userAgent;
  }

  @Override
  public String getSession() {
    return session;
  }
}
