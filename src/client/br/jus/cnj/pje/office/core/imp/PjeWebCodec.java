package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CancellationException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.github.utils4j.IConstants;
import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;

class PjeWebCodec extends SocketCodec<HttpUriRequestBase> {

  private static boolean isSuccess(int code) {
    return code < HttpStatus.SC_REDIRECTION; //Não seria HttpStatus.SC_BAD_REQUEST ?;
  }

  private final CloseableHttpClient client;
  
  public PjeWebCodec(CloseableHttpClient client) {
    this.client = Args.requireNonNull(client, "client is null");
  }
  
  @Override
  public void close() throws Exception {
    client.close();
  }

  @Override
  public PjeTaskResponse post(final Supplier<HttpUriRequestBase> supplier, IResultChecker checkResults) throws Exception {
    try {
      final HttpPost post = (HttpPost)supplier.get();

      try(CloseableHttpResponse response = client.execute(post)) {
        int code = response.getCode();
        if (!isSuccess(code)) { 
          throw new PJeClientException("Servidor retornando - HTTP Code: " + code);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String responseText;
          try {
            responseText = EntityUtils.toString(entity, IConstants.DEFAULT_CHARSET);
          } catch (ParseException | IOException e) {
            throw new PJeClientException("Falha na leitura de entity - HTTP Code: " + code, e);
          } finally {
            EntityUtils.consumeQuietly(entity);
          }
          checkResults.run(responseText);
        }
        return PjeWebTaskResponse.SUCCESS;
      }
    }catch(CancellationException e) {
      throw new PJeClientException("Operação cancelada. Os dados não foram enviados ao servidor.", e);
    }
  }
  
  @Override
  public void get(Supplier<HttpUriRequestBase> supplier, IDownloadStatus status) throws Exception {
    
    try(OutputStream output = status.onNewTry(1)) {
      final HttpGet get = (HttpGet)supplier.get();
      
      try(CloseableHttpResponse response = client.execute(get)) {
        int code = response.getCode();

        if (!isSuccess(code)) { 
          throw new PJeClientException("Servidor retornando - HTTP Code: " + code);
        }
        HttpEntity entity = response.getEntity();
        if (entity == null) {
          throw new PJeClientException("Servidor não foi capaz de retornar dados. (entity is null) - HTTP Code: " + code);
        }
        try {
          final long total = entity.getContentLength();
          final InputStream input = entity.getContent();
          
          status.onStartDownload(total);
          final byte[] buffer = new byte[128 * 1024];
          
          status.onStatus(total, 0);
          for(int length, written = 0; (length = input.read(buffer)) > 0; status.onStatus(total, written += length))
            output.write(buffer, 0, length);
          status.onEndDownload();
            
        } catch(InterruptedException e) {
          throw new PJeClientException("Download interrompido - HTTP Code: " + code, e);
        } catch(Exception e) {
          throw new PJeClientException("Falha durante o download do arquivo - HTTP Code: " + code, e);
        } finally {
          EntityUtils.consumeQuietly(entity);
        }
      } finally {
        get.clear();
      }
    } catch (Exception e) {
      status.onDownloadFail(e);
      throw e;
    }
  }
}
