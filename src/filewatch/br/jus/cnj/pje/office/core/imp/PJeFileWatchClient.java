package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import org.json.JSONObject;

import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeFileWatchClient extends PjeClientWrapper {

  PJeFileWatchClient(Version version, Charset charset) {
    super(new PJeJsonClient(version, new PjeFileJsonCodec(charset))); 
  }
  
  private static class PjeFileJsonCodec extends SocketCodec<JSONObject> {
    private Charset charset;

    PjeFileJsonCodec(Charset charset) {
      this.charset = Args.requireNonNull(charset, "charset is null");
    }
    
    @Override
    public PjeTaskResponse post(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeFileWatchTaskResponse(supplier.get().toString(), charset);
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
