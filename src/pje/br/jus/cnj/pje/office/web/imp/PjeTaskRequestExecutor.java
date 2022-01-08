package br.jus.cnj.pje.office.web.imp;

import java.util.concurrent.atomic.AtomicBoolean;

import com.github.signer4j.imp.Args;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.IProgressView;
import com.github.signer4j.task.imp.TaskRequestExecutor;

import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.core.ITaskExecutorParams;
import br.jus.cnj.pje.office.web.IPjeRequest;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeTaskRequestExecutor extends TaskRequestExecutor<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  
  private final IPjeTokenAccess tokenAccess;
  private final IPjeSecurityAgent securityAgent;
  private final AtomicBoolean localRequest;
  
  public PjeTaskRequestExecutor(IProgressFactory factory, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent, AtomicBoolean localRequest) {
    super(PjeRequestResolver.INSTANCE, factory);
    this.tokenAccess = Args.requireNonNull(tokenAccess, "tokenAccess is null");
    this.securityAgent = Args.requireNonNull(securityAgent, "securityAgent is null");
    this.localRequest = Args.requireNonNull(localRequest, "localRequest is null");
  }
  
  @Override
  protected void onRequestResolved(PjeTaskRequest request) {
    request.of(ITaskExecutorParams.PJE_REQUEST_LOCAL, localRequest);
    request.of(ITaskExecutorParams.PJE_REQUEST_EXECUTOR, getExecutor());
    request.of(IPjeTokenAccess.TOKEN_ACCESS, tokenAccess);
    request.of(IPjeSecurityAgent.PJE_SECURITY_AGENT_PARAM, securityAgent);
  }

  @Override
  protected void endExecution(IProgressView progress) {
    this.localRequest.set(false);
    super.endExecution(progress);
  }
}

