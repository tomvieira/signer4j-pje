package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.json.JSONObject;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeClipClient extends PjeClientWrapper {

  PJeClipClient(Version version, Charset charset) {
    super(new PJeJsonClient(version, new PjeClipJsonCodec(charset))); 
  }
  
  PJeClipClient(Version version, Charset charset, CloseableHttpClient client) {
    super(new PjeWebClient(client, version));
  }
  
  private static class PjeClipJsonCodec extends SocketCodec<JSONObject> {
    private final Charset charset;
    
    PjeClipJsonCodec(Charset charset) {
      this.charset = Args.requireNonNull(charset,  "charset is null");
    }
    
    @Override
    public PjeTaskResponse post(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeClipTaskResponse(supplier.get().toString(), charset);
    }

    @Override
    public void get(Supplier<JSONObject> supplier, IDownloadStatus status) throws Exception {
      throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
    }
  }
}
