package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import org.json.JSONObject;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeClipClient extends PjeClientWrapper {

  PJeClipClient(Version version, Charset charset) {
    super(new PJeJsonClient(version, new PjeClipCodec(charset))); 
  }
  
  private static class PjeClipCodec extends SocketCodec<JSONObject> {
    private final Charset charset;
    
    PjeClipCodec(Charset charset) {
      this.charset = Args.requireNonNull(charset,  "charset is null");
    }
    
    @Override
    protected PjeTaskResponse doPost(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeClipTaskResponse(supplier.get().toString(), charset);
    }

    @Override
    protected void doGet(Supplier<JSONObject> supplier, IDownloadStatus status) throws Exception {
      throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
    }
  }
}
