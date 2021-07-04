/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.core.imp;

import com.github.progress4j.IProgressFactory;
import com.github.taskresolver4j.imp.TaskRequestExecutor;
import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.IPjeSecurityAgent;
import br.jus.cnj.pje.office.core.IPjeSecurityPermissor;
import br.jus.cnj.pje.office.core.IPjeTokenAccess;
import br.jus.cnj.pje.office.task.ITaskExecutorParams;

class PjeTaskRequestExecutor extends TaskRequestExecutor<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  
  private final IPjeTokenAccess tokenAccess;
  private final IPjeSecurityPermissor securityAgent;
  
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

