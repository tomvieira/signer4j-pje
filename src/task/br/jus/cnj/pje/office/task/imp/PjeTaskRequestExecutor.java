package br.jus.cnj.pje.office.task.imp;

import com.github.signer4j.imp.Args;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.task.imp.TaskRequestExecutor;

import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.task.ITaskExecutorParams;

public class PjeTaskRequestExecutor extends TaskRequestExecutor<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  
  private final IPjeTokenAccess tokenAccess;
  private final IPjeSecurityAgent securityAgent;
  
  public PjeTaskRequestExecutor(IProgressFactory factory, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent) {
    super(PjeRequestResolver.INSTANCE, factory);
    this.tokenAccess = Args.requireNonNull(tokenAccess, "tokenAccess is null");
    this.securityAgent = Args.requireNonNull(securityAgent, "securityAgent is null");
  }
  
  @Override
  protected void onRequestResolved(PjeTaskRequest request) {
    request.of(ITaskExecutorParams.PJE_REQUEST_LOCAL, localRequest);
    request.of(ITaskExecutorParams.PJE_REQUEST_EXECUTOR, executor);
    request.of(IPjeTokenAccess.PARAM_NAME, tokenAccess);
    request.of(IPjeSecurityAgent.PARAM_NAME, securityAgent);
  }
}

