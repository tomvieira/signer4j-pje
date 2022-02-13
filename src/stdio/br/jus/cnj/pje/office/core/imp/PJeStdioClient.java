package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import org.json.JSONObject;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeStdioClient extends PjeClientWrapper {

  PJeStdioClient(Version version, Charset charset) {
    super(new PJeJsonClient(version, new PjeStdioCodec(charset))); 
  }
  
  private static class PjeStdioCodec extends SocketCodec<JSONObject> {
    private Charset charset;

    PjeStdioCodec(Charset charset) {
      this.charset = Args.requireNonNull(charset, "charset is null");
    }
    
    @Override
    protected PjeTaskResponse doPost(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeStdioTaskResponse(supplier.get().toString(), charset);
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
