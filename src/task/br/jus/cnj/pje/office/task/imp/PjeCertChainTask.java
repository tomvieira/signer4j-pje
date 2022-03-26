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


package br.jus.cnj.pje.office.task.imp;

import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.ITarefaCertChain;

class PjeCertChainTask extends PjeAbstractTask<ITarefaCertChain> {

  private String uploadUrl;
  
  public PjeCertChainTask(Params params, ITarefaCertChain pojo) {
    super(params, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaCertChain pojo = getPojoParams();
    this.uploadUrl = PjeTaskChecker.checkIfPresent(pojo.getUploadUrl(),  "uploadUrl");
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException {
    IPjeToken token = loginToken();
    try {
      String certificateChain64;
      try {
        certificateChain64 = token.createChooser().choose().getCertificateChain64();
      } catch (Exception e) {
        throw new TaskException("Escolha do certificado cancelada", e);
      }
      
      try {
        return getPjeClient().send(
          getTarget(uploadUrl),
          certificateChain64
        );
      } catch (PJeClientException e) {
        throw new TaskException("Não foi possível enviar cadeia de certificados", e);
      }
    }finally {
      token.logout();
    }
  }
}
