package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PjeWebClient.ResultChecker.IF_ERROR_THROW;
import static br.jus.cnj.pje.office.core.imp.PjeWebClient.ResultChecker.IF_NOT_SUCCESS_THROW;
import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Args.requireText;
import static com.github.signer4j.imp.Strings.trim;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import com.github.signer4j.IContentType;
import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Objects;
import com.github.signer4j.imp.function.Runnable;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.web.IPjeHeaders;

class PjeWebClient extends AstractPjeClient<HttpUriRequestBase> {

  private static boolean isSuccess(int code) {
    return code < HttpStatus.SC_REDIRECTION; //Não seria HttpStatus.SC_BAD_REQUEST ?;
  }
  
  private final CloseableHttpClient client;
  
  PjeWebClient(CloseableHttpClient client, Version version) {
    super(version, IF_ERROR_THROW, IF_NOT_SUCCESS_THROW);
    this.client = requireNonNull(client, "client is null");
  }
 
  @Override
  public void close() throws IOException {
    this.client.close();
  }
  
  @Override
  protected <T extends HttpUriRequestBase> T createOutput(T request, String session, String userAgent) {
    request.setHeader(HttpHeaders.COOKIE, session);
    request.setHeader(IPjeHeaders.VERSION, version.toString());
    request.setHeader(HttpHeaders.USER_AGENT, userAgent);
    canceller.cancelCode(request::abort);
    return request;
  }

  private HttpPost createPost(String endPoint, String session, String userAgent) {
    return createOutput(new HttpPost(endPoint), session, userAgent);
  }
  
  private HttpGet createGet(String endPoint, String session, String userAgent) {
    return createOutput(new HttpGet(endPoint), session, userAgent);
  }
  
  @Override
  protected HttpPost createOutput(String endPoint, String session, String userAgent, ISignedData signedData) throws Exception {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    postRequest.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("assinatura", signedData.getSignature64()),
      new BasicNameValuePair("cadeiaCertificado", signedData.getCertificateChain64())
    )));
    return postRequest;
  }

  @Override
  protected HttpPost createOutput(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws Exception {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final List<NameValuePair> parameters = Arrays.asList(
      new BasicNameValuePair("assinatura", signedData.getSignature64()),
      new BasicNameValuePair("cadeiaCertificado", signedData.getCertificateChain64()),
      new BasicNameValuePair("id", file.getId().orElse("")),
      new BasicNameValuePair("codIni", file.getCodIni().orElse("")),
      new BasicNameValuePair("hash", file.getHash().get()));
    if (file.getIdTarefa().isPresent())
      parameters.add(new BasicNameValuePair("idTarefa", file.getIdTarefa().get().toString())); 
    postRequest.setEntity(new UrlEncodedFormEntity(parameters));
    return postRequest;
  }
  
  @Override
  protected HttpPost createOutput(String endPoint, String session, String userAgent, IArquivoAssinado file, IContentType contentType) {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addPart(file.getFileFieldName(), new ByteArrayBody(
      file.getSignedData().get().getSignature(), 
      ContentType.create(contentType.getMineType(), contentType.getCharset()),
      file.getNome().get() + contentType.getExtension()
    ));
    file.getParamsEnvio().stream().map(param -> {
      int idx = (param = trim(param)).indexOf('=');
      return new BasicNameValuePair(
        idx < 0 ? param : param.substring(idx),  
        idx < 0 ? ""    : param.substring(idx + 1)
      );
    }).forEach(nv -> builder.addPart(nv.getName(), new StringBody(nv.getValue(), ContentType.TEXT_PLAIN)));
    postRequest.setEntity(builder.build());
    return postRequest;
  }
  
  @Override
  protected HttpPost createOutput(String endPoint, String session, String userAgent, String certificateChain64) throws Exception  {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    postRequest.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("cadeiaDeCertificadosBase64", certificateChain64)
    )));
    return postRequest;
  }
  
  @Override
  protected HttpPost createOutput(String endPoint, String session, String userAgent, Object pojo) throws Exception {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    postRequest.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    postRequest.setEntity(new StringEntity(Objects.toJson(pojo), ContentType.APPLICATION_JSON));
    return postRequest;
  }

  @Override
  public void down(String endPoint, String session, String userAgent, IDownloadStatus status) throws PJeClientException {
    requireNonNull(status, "status is null");
    final Supplier<HttpGet> supplier = () -> createGet(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null")
    );
    get(supplier, status);
  }
  
  @Override
  protected void post(final Supplier<HttpUriRequestBase> supplier, Runnable<String, PJeClientException> checkResults) throws PJeClientException {
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
            responseText = EntityUtils.toString(entity, Constants.DEFAULT_CHARSET);
          } catch (ParseException | IOException e) {
            throw new PJeClientException("Falha na leitura de entity - HTTP Code: " + code, e);
          } finally {
            EntityUtils.consumeQuietly(entity);
          }
          checkResults.run(responseText);
        }
      }
    }catch(PJeClientException e) {
      throw e;
    }catch(CancellationException e) {
      throw new PJeClientException("Operação cancelada. Os dados não foram enviados ao servidor.", e);
    }catch(Exception e) {
      throw new PJeClientException("Não foi possivel enviar dados ao servidor. ", e);
    }
  }
  
  private void get(Supplier<HttpGet> supplier, IDownloadStatus status) throws PJeClientException {
    try {
      final HttpGet get = supplier.get();

      try(CloseableHttpResponse response = client.execute(get)) {
        int code = response.getCode();

        HttpEntity entity = response.getEntity();
        if (entity == null) {
          throw new PJeClientException("Servidor não foi capaz de retornar dados. (entity is null) - HTTP Code: " + code);
        }
        
        try(OutputStream output = status.onNewTry(1)) {
          
          final long total = entity.getContentLength();
          final InputStream input = entity.getContent();
          
          status.onStartDownload(total);
          final byte[] buffer = new byte[128 * 1024];
          
          status.onStatus(total, 0);
          for(int length, written = 0; (length = input.read(buffer)) > 0; status.onStatus(total, written += length))
            output.write(buffer, 0, length);
          status.onEndDownload();
        
        } catch(Exception e) {
          status.onDownloadFail(e);
          throw new PJeClientException("Falha durante o download do arquivo - HTTP Code: " + code, e);
        } finally {
          EntityUtils.consumeQuietly(entity);
        }
      } finally {
        get.clear();
      }
    } catch(PJeClientException e) {
      throw e;
    } catch(CancellationException e) {
      throw new PJeClientException("Download cancelado!", e);
    } catch(Exception e) {
      throw new PJeClientException("Não foi possivel baixar dados do servidor.", e);
    }
  }
  
  protected static enum ResultChecker implements AstractPjeClient.ResultChecker {
    
    IF_ERROR_THROW() {
      @Override
      public void run(String response) throws PJeClientException {
        final int length = response.length();
        if (response.startsWith(SERVER_FAIL_RESPONSE)) { 
          String message = length > SERVER_FAIL_RESPONSE.length() ? 
            response.substring(SERVER_FAIL_RESPONSE.length()) : 
            "Desconhecido";
          throw new PJeClientException("Servidor retornou Erro: '" + message.trim() + "'");
        }
      }
    },
    
    IF_NOT_SUCCESS_THROW() {
      @Override
      public void run(String response) throws PJeClientException {
        if (response.startsWith(SERVER_SUCCESS_RESPONSE))
          return;
        IF_ERROR_THROW.run(response);
        throw new PJeClientException("Servidor não recebeu dados enviados");
      }
    };
    
    private static final String SERVER_SUCCESS_RESPONSE = "Sucesso";
    private static final String SERVER_FAIL_RESPONSE    = "Erro:"; 
  }
}

