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

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Pair;

import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

public final class SimpleContext implements IPjeContext {

  private final Pair<IPjeRequest, IPjeResponse> pair;
  
  public static SimpleContext of(IPjeRequest request, IPjeResponse response) {
    Args.requireNonNull(request, "request is null");
    Args.requireNonNull(response, "response is null");
    return new SimpleContext(request, response);
  }
  
  private SimpleContext(IPjeRequest request, IPjeResponse response) {    
    pair = Pair.of(request, response);    
  }
  
  @Override
  public final String getId() {
    return getRequest().getId();
  }  

  @Override
  public final IPjeRequest getRequest() {
    return pair.getKey();
  }

  @Override
  public final IPjeResponse getResponse() {
    return pair.getValue();
  }
}
