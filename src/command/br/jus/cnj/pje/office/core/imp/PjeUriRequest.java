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

import static com.github.utils4j.imp.Strings.trim;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeRequest;

public abstract class PjeUriRequest implements IPjeRequest {

  private final URI uri;
  
  private final String userAgent;

  private final List<NameValuePair> queryParams;
  
  private final Optional<String> origin;

  private final boolean post;
  
  protected PjeUriRequest(URI input, String userAgent, String origin, boolean post) {
    this.queryParams = parseParams(this.uri = input);
    this.userAgent = trim(userAgent, "Unknown");
    this.origin = Strings.optional(origin);
    this.post = post;
  }

  @Override
  public final Optional<String> getParameterR() {
    return getParameter(PJE_REQUEST_PARAMETER_NAME);
  }

  @Override
  public final Optional<String> getParameterU() {
    return getParameter(PJE_REQUEST_PARAMETER_CACHE);
  }
  
  @Override
  public final Optional<String> getUserAgent() {
    return Optional.ofNullable(userAgent);
  }
  
  @Override
  public final Optional<String> getOrigin() {
    return origin;
  }
  
  @Override
  public final boolean isPost() {
    return post;
  }
  
  @Override
  public final String getId() {
    return uri.toString();
  }
  
  @Override
  public final String toString() {
    return getId();
  }
  
  protected Optional<String> getParameter(String key) {
    return queryParams.stream().filter(n -> n.getName().equalsIgnoreCase(key)).map(n -> n.getValue()).findFirst();
  }
  
  private static List<NameValuePair> parseParams(URI uri) {
    return new URIBuilder(uri).getQueryParams();
  }  
}
