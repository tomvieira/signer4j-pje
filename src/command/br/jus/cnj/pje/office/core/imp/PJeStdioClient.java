package br.jus.cnj.pje.office.core.imp;

import org.json.JSONObject;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

public class PJeStdioClient extends PjeClientWrapper {

  PJeStdioClient(Version version) {
    super(new PJeJsonClient(version, new PjeStdioCodec())); 
  }
  
  private static class PjeStdioCodec extends SocketCodec<JSONObject> {
    @Override
    protected PjeTaskResponse doPost(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeStdioTaskResponse(supplier.get().toString());
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
