package br.jus.cnj.pje.office.web.imp;

import java.util.concurrent.atomic.AtomicBoolean;

import com.github.signer4j.imp.Args;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.task.imp.TaskRequestExecutor;

import br.jus.cnj.pje.office.core.IPjeProgressView;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.web.IPjeRequest;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeTaskRequestExecutor extends TaskRequestExecutor<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  
  private final IPjeTokenAccess tokenAccess;
  private final IPjeSecurityAgent securityAgent;
  private final IPjeProgressView view;
  private final AtomicBoolean localRequest;
  
  public PjeTaskRequestExecutor(IPjeProgressView view, IProgressFactory factory, IPjeTokenAccess tokenAccess, IPjeSecurityAgent securityAgent, AtomicBoolean localRequest) {
    super(PjeRequestResolver.INSTANCE, factory);
    this.view = Args.requireNonNull(view, "view is null");
    this.tokenAccess = Args.requireNonNull(tokenAccess, "tokenAccess is null");
    this.securityAgent = Args.requireNonNull(securityAgent, "securityAgent is null");
    this.localRequest = Args.requireNonNull(localRequest, "localRequest is null");
  }
  
  @Override
  protected void onRequestResolved(PjeTaskRequest request) {
    request.of(IPjeRequest.PJE_LOCAL_REQUEST, localRequest);
    request.of(IPjeTokenAccess.TOKEN_ACCESS, tokenAccess);
    request.of(IPjeSecurityAgent.PJE_SECURITY_AGENT_PARAM, securityAgent);
  }

  @Override
  protected void beginExecution(IProgress progress) {
    super.beginExecution(progress);
    view.display();
  }
  
  @Override
  protected void endExecution(IProgress progress) {
    this.localRequest.set(false);
    super.endExecution(progress);
    view.undisplay();
  }
}

