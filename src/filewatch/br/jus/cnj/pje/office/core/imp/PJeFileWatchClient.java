package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
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
    public void get(Supplier<HttpUriRequestBase> supplier, IDownloadStatus status) throws Exception {
      codec.get(supplier, status);
    }

    @Override
    public void close() throws Exception {
    }
  }
}
