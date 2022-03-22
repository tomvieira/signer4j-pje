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

class PJeClipClient extends PjeClientWrapper {

  PJeClipClient(Version version, Charset charset, IGetCodec downloader) {
    super(new PJeJsonClient(version, new PjeClipJsonCodec(charset, downloader)));
  }
  
  private static class PjeClipJsonCodec extends SocketCodec<JSONObject> {
    private final IGetCodec codec;
    private final Charset charset;
    
    PjeClipJsonCodec(Charset charset, IGetCodec downloader) {
      this.charset = Args.requireNonNull(charset,  "charset is null");
      this.codec = Args.requireNonNull(downloader, "downloader is null");
    }
    
    @Override
    public PjeTaskResponse post(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeClipTaskResponse(supplier.get().toString(), charset);
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
