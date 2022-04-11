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

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import org.apache.hc.core5.http.HttpHeaders;

import com.github.taskresolver4j.IRequestResolver;
import com.github.taskresolver4j.exception.TaskResolverException;

import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.task.IMainParams;
import br.jus.cnj.pje.office.task.imp.FullMain;

enum PjeRequestResolver implements IRequestResolver<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  INSTANCE;

  @Override
  public PjeTaskRequest resolve(IPjeRequest request) throws TaskResolverException {

    final Optional<String> u = request.getParameterU();
    if (!u.isPresent()) {
      throw new TaskResolverException("Parâmetro 'u' não faz parte da requisição! (browser cache issue)");
    }
	    
    final Optional<String> r = request.getParameterR();
    if (!r.isPresent()) {
      throw new TaskResolverException("Unabled to resolve task with empty request 'r' param");
    }
    
    final Function<IMainParams, IMainParams> wrapper = m -> new FullMain(m, request);
    
    PjeTaskRequest tr;
    try {
      tr = MAIN.read(r.get(), 
        new PjeTaskRequest()
        .of(IPjeRequest.PJE_REQUEST_IS_POST, request.isPost())
        .of(HttpHeaders.USER_AGENT, request.getUserAgent()), 
        wrapper
      );
    } catch (IOException e) {
      throw new TaskResolverException("Unabled to read 'r' request parameter: " + r.get(), e);
    }
    
    StringBuilder because = new StringBuilder();
    if (!tr.isValid(because)) {
      throw new TaskResolverException("Unabled to read 'r' request parameter because: " + because);
    }
    
    return tr;
  }
}
