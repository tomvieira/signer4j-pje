package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.SimpleContext.of;
import static com.github.signer4j.imp.Throwables.tryRun;

import java.io.IOException;
import java.io.InputStream;

import com.github.signer4j.IFinishable;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.InterruptibleInputStream;

import br.jus.cnj.pje.office.core.IPjeContext;

class PjeStdioServer extends PjeTextServer {
  
  private static final int MAX_BODY_SIZE = 8 * 1028 * 1024;
  
  private final InputStream stdin = new InterruptibleInputStream(System.in);

  public PjeStdioServer(IFinishable finishingCode) {
    super(finishingCode, "stdio://native-messaging");
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
  protected IPjeContext createContext(String input) throws Exception {
    return of(new PjeStdioRequest(input), new PjeStdioResponse());
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
      return new String(bodyUrl, Constants.DEFAULT_CHARSET);
    }while(true);
  }
  
  private static int toInt(byte[] bytes) {
    return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000 | (bytes[1] << 8) & 0x0000ff00 | (bytes[0] << 0) & 0x000000ff;
  }
}

