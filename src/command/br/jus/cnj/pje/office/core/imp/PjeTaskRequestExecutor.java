package br.jus.cnj.pje.office.core.imp;

import com.github.progress4j.IProgressFactory;
import com.github.taskresolver4j.imp.TaskRequestExecutor;
import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.task.ITaskExecutorParams;

class PjeTaskRequestExecutor extends TaskRequestExecutor<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  
  private final IPjeTokenAccess tokenAccess;
  private final IPjeSecurityAgent securityAgent;
  
  public PjeTaskRequestExecutor(IProgressFactory factory, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent) {
    super(PjeRequestResolver.INSTANCE, factory);
    this.tokenAccess = Args.requireNonNull(tokenAccess, "tokenAccess is null");
    this.securityAgent = Args.requireNonNull(securityAgent, "securityAgent is null");
  }
  
  @Override
  protected void onRequestResolved(PjeTaskRequest request) {
    request.of(ITaskExecutorParams.PJE_REQUEST_EXECUTOR, executor);
    request.of(IPjeTokenAccess.PARAM_NAME, tokenAccess);
    request.of(IPjeSecurityAgent.PARAM_NAME, securityAgent);
  }
}

