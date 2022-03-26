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
import org.json.JSONObject;

import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IGetCodec;
import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeFileWatchClient extends PjeClientWrapper {

  PJeFileWatchClient(Version version, Charset charset, IGetCodec codec) {
    super(new PJeJsonClient(version, new PjeFileJsonCodec(charset, codec))); 
  }
  
  private static class PjeFileJsonCodec extends SocketCodec<JSONObject> {
    private final Charset charset;
    private final IGetCodec codec;

    PjeFileJsonCodec(Charset charset, IGetCodec codec) {
      this.charset = Args.requireNonNull(charset, "charset is null");
      this.codec = Args.requireNonNull(codec, "codec is null");
    }
    
    @Override
    public PjeTaskResponse post(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeFileWatchTaskResponse(supplier.get().toString(), charset);
    }

    @Override
    public void get(Supplier<HttpGet> supplier, IDownloadStatus status) throws Exception {
      codec.get(supplier, status);
    }

    @Override
    public void close() throws Exception {
    }
  }
}
