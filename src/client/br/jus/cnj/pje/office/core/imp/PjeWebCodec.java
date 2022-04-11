/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CancellationException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
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

class PjeWebCodec extends SocketCodec<HttpPost> {

  private static boolean isSuccess(int code) {
    return (code >= HttpStatus.SC_SUCCESS && code < HttpStatus.SC_REDIRECTION) || code == HttpStatus.SC_NOT_MODIFIED; 
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
  public PjeTaskResponse post(final Supplier<HttpPost> supplier, IResultChecker checkResults) throws Exception {
    try {      
      final HttpPost post = supplier.get();

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
        return PjeWebTaskResponse.success();
      }
    }catch(CancellationException e) {
      throw new PJeClientException("Operação cancelada. Os dados não foram enviados ao servidor.", e);
    }
  }
  
  @Override
  public void get(Supplier<HttpGet> supplier, IDownloadStatus status) throws Exception {
    
    try(OutputStream output = status.onNewTry()) {
      final HttpGet get = supplier.get();
      
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
