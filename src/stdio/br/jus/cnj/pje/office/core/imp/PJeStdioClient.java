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

import java.nio.charset.Charset;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.json.JSONObject;

import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IGetCodec;
import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeStdioClient extends PjeClientWrapper {

  PJeStdioClient(Version version, Charset charset, IGetCodec codec) {
    super(new PJeJsonClient(version, new PjeStdioJsonCodec(charset, codec))); 
  }
  
  /**
   * Nesta implementação o navegador recebe os dados via JSON e é ele quem envia
   * dados para o servidor.
   * @author Leonardo
   */
  private static class PjeStdioJsonCodec extends SocketCodec<JSONObject> {
    private final Charset charset;
    private final IGetCodec codec;

    PjeStdioJsonCodec(Charset charset, IGetCodec codec) {
      this.charset = Args.requireNonNull(charset, "charset is null");
      this.codec = Args.requireNonNull(codec, "codec is null");
    }
    
    @Override
    public PjeTaskResponse post(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeStdioTaskResponse(supplier.get().toString(), charset);
    }

    @Override
    public void get(Supplier<HttpGet> supplier, IDownloadStatus status) throws Exception {
      codec.get(supplier, status);
    }

    @Override
    public void close() throws Exception {
    }
  }
  
  /**
   * Nesta implementação é o assinador quem envia dados ao servidor e o 
   * navegador apenas é notificado de sucesso/falha
   * @author Leonardo
   */
  private static class PjeStdioWebCodec extends PjeWebCodec {
    
    PjeStdioWebCodec(CloseableHttpClient client) {
      super(client);
    }
    
    @Override
    public PjeTaskResponse post(final Supplier<HttpPost> supplier, IResultChecker checkResults) throws Exception {
      try {
        super.post(supplier, checkResults);
        return PjeClientMode.STDIO.success().apply("success");
      }catch(Exception e) {
        return PjeClientMode.STDIO.fail().apply(e);
      }
    }
  }
}
