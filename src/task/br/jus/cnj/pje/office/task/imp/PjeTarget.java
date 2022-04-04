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

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.task.IPjeTarget;

class PjeTarget implements IPjeTarget {

  private final String endPoint;
  private final String userAgent;
  private final String session;
  private final boolean responseJson;

  PjeTarget(String endPoint, String userAgent, String session, boolean responseJson) {
    this.endPoint = Args.requireText(endPoint, "endpoint is null");
    this.userAgent = Args.requireNonNull(userAgent, "userAgent is null");
    this.session = Args.requireNonNull(session, "session is null");//single sign on has empty string session but not null
    this.responseJson = responseJson;
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

  @Override
  public boolean isResponseJson() {
    return responseJson;
  }
}
