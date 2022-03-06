package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.io.InputStream;

import com.github.utils4j.IConstants;
import com.github.utils4j.imp.InterruptibleInputStream;

import br.jus.cnj.pje.office.IBootable;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeStdioServer extends PjeURIServer {
  
  private static final int MAX_BODY_SIZE = 8 * 1028 * 1024;
  
  private final InputStream stdin = new InterruptibleInputStream(System.in);

  public PjeStdioServer(IBootable boot) {
    super(boot, "stdio://native-messaging");
  }
  
  private void skip() throws IOException {
    int skip;
    while((skip = stdin.available()) > 0)
      stdin.skip(skip);
  }

  @Override
  protected void clearBuffer() {
    tryRun(this::skip);
  }
  
  @Override
  protected IPjeResponse createResponse() throws Exception {
    return new PjeStdioResponse();
  }

  @Override
  protected IPjeRequest createRequest(String uri, String origin) throws Exception {
    return new PjeStdioRequest(uri, origin);
  }

  @Override
  protected String getUri() throws InterruptedException, Exception {
    byte[] header = new byte[4];
    do {
      int read;
      if ((read = stdin.read(header)) < 0) {
        throw new IOException("Leitura negativa " + read);
      }
      final int size = toInt(header);
      if (size <= 0 || size > MAX_BODY_SIZE) {
        throw new IOException("Header de tamanho inválido: " + size);
      }
      byte[] bodyUrl = new byte[size];
      if ((read = stdin.read(bodyUrl)) != size) {
        throw new IOException("Body de tamanho inválido: " + size + ". Esperado = " + read);
      }
      return new String(bodyUrl, IConstants.DEFAULT_CHARSET);
    }while(true);
  }
  
  private static int toInt(byte[] bytes) {
    return
      (bytes[3] << 24) & 0xFF000000 | 
      (bytes[2] << 16) & 0x00FF0000 | 
      (bytes[1] << 8)  & 0x0000FF00 | 
      (bytes[0] << 0)  & 0x000000FF;
  }
}

