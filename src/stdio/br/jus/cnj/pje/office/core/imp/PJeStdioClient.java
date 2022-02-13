package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.json.JSONObject;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;

class PJeStdioClient extends PjeClientWrapper {

  PJeStdioClient(Version version, Charset charset) {
    super(new PJeJsonClient(version, new PjeStdioJsonCodec(charset))); 
  }
  
  PJeStdioClient(CloseableHttpClient client, Version version) {
    super(new PjeWebClient(new PjeStdioWebCodec(client), version));
  }
  
  /**
   * Nesta implementação o navegador recebe os dados via JSON e é ele quem envia
   * dados para o servidor.
   * @author Leonardo
   */
  private static class PjeStdioJsonCodec extends SocketCodec<JSONObject> {
    private Charset charset;

    PjeStdioJsonCodec(Charset charset) {
      this.charset = Args.requireNonNull(charset, "charset is null");
    }
    
    @Override
    public PjeTaskResponse post(Supplier<JSONObject> supplier, IResultChecker checker) throws Exception {
      return new PjeStdioTaskResponse(supplier.get().toString(), charset);
    }

    @Override
    public void get(Supplier<JSONObject> supplier, IDownloadStatus status) throws Exception {
      throw new UnsupportedOperationException();
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
    public PjeTaskResponse post(final Supplier<HttpUriRequestBase> supplier, IResultChecker checkResults) throws Exception {
      try {
        super.post(supplier, checkResults);
        return PjeClientMode.STDIO.success().apply("success");
      }catch(Exception e) {
        return PjeClientMode.STDIO.fail().apply(e);
      }
    }
  }
}
