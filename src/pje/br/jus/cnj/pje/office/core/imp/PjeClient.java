package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Args.requireText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Objects;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.imp.function.Runnable;
import com.github.signer4j.imp.function.Supplier;
import com.github.signer4j.progress.imp.IAttachable;

import br.jus.cnj.pje.office.core.IArquivoAssinado;
import br.jus.cnj.pje.office.core.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.core.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.core.IDadosSSO;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeClient implements IPjeClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(PjeClient.class);
  
  private static boolean isSuccess(int code) {
    return code < 400;
  }
  
  private static String toJson(Object instance) throws PJeClientException {
    try {
      return Objects.toJson(instance);
    } catch (JsonProcessingException e) {
      throw new PJeClientException("Não foi possível serializar instancia em Json", e);
    }    
  }  

  private final Version version;
  private final CloseableHttpClient client;
  private IAttachable attachable = (r) -> {};

  PjeClient(CloseableHttpClient client, Version version) {
    this.client = requireNonNull(client, "client is null");
    this.version = requireNonNull(version, "version is null");
  }
  
  void setAttachable(IAttachable attachable) {
    this.attachable = attachable == null ? this.attachable : attachable;
  }
 
  @Override
  public void close() throws IOException {
    this.client.close();
  }
  
  private <T extends HttpUriRequestBase> T createRequest(T request, String session, String userAgent) {
    request.setHeader(HttpHeaders.COOKIE, session);
    request.setHeader("versao", version.toString());
    request.setHeader(HttpHeaders.USER_AGENT, userAgent);
    attachable.attach(request::abort);
    return request;
  }

  private HttpPost createPost(String endPoint, String session, String userAgent) {
    return createRequest(new HttpPost(endPoint), session, userAgent);
  }
  
  private HttpGet createGet(String endPoint, String session, String userAgent) {
    return createRequest(new HttpGet(endPoint), session, userAgent);
  }
  
  private HttpPost createPostRequest(String endPoint, String session, String userAgent, ISignedData signedData) throws Exception {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("assinatura", signedData.getSignature64()));
    parameters.add(new BasicNameValuePair("cadeiaCertificado", signedData.getCertificateChain64()));
    postRequest.setEntity(new UrlEncodedFormEntity(parameters));
    return postRequest;
  }

  private HttpPost createPostRequest(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws Exception {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("assinatura", signedData.getSignature64()));
    parameters.add(new BasicNameValuePair("cadeiaCertificado", signedData.getCertificateChain64()));
    parameters.add(new BasicNameValuePair("id", file.getId().get()));
    parameters.add(new BasicNameValuePair("codIni", file.getCodIni().get()));
    parameters.add(new BasicNameValuePair("hash", file.getHash().get()));
    if (file.getIdTarefa().isPresent())
      parameters.add(new BasicNameValuePair("idTarefa", file.getIdTarefa().get().toString())); 
    postRequest.setEntity(new UrlEncodedFormEntity(parameters));
    return postRequest;
  }
  
  private HttpPost createPostRequest(String endPoint, String session, String userAgent, IArquivoAssinado file) {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final ISignedData signedData  = file.getSignedData().get();
    final String fileName         = file.getNome().get();
    final List<String> sendParams = file.getParamsEnvio();
    final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addPart(file.getFileFieldName(), new ByteArrayBody(signedData.getSignature(), fileName));
    sendParams.forEach(s -> {
      String param = Strings.trim(s);
      if (param.length() < 3) {
        LOGGER.warn("Parametros com formato inválido (não será enviado): '{}'", param);
        return;
      }
      int idx = s.indexOf('=');
      if (idx < 0) {
        LOGGER.warn("Parametros com formato inválido (não será enviado): '{}'", param);
        return;
      }
      final String key = s.substring(0, idx).trim();
      final String value = s.substring(idx + 1).trim();
      builder.addPart(key, new StringBody(value, ContentType.TEXT_PLAIN));
    });
    postRequest.setEntity(builder.build());
    return postRequest;
  }
  
  private HttpPost createPostRequest(String endPoint, String session, String userAgent, String certificateChain64) throws Exception  {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    List<NameValuePair> parameters = new ArrayList<>();
    parameters.add(new BasicNameValuePair("cadeiaDeCertificadosBase64", certificateChain64));
    postRequest.setEntity((HttpEntity)new UrlEncodedFormEntity(parameters));
    return postRequest;
  }
  
  private HttpPost createJsonPostRequest(String endPoint, String session, String userAgent, Object pojo) throws Exception {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    postRequest.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    postRequest.setEntity(new StringEntity(toJson(pojo), ContentType.APPLICATION_JSON));
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
  public void send(String endPoint, String session, String userAgent, ISignedData signedData) throws PJeClientException {
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(signedData, "signed data null")
    );
    post(supplier, ResultChecker.IF_ERROR_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException {
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(signedData, "signedData null"),
      requireNonNull(file, "file is null")
    );
    post(supplier, ResultChecker.IF_NOT_SUCCESS_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, IArquivoAssinado file) throws PJeClientException {
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(file, "file is null")
    );
    post(supplier, ResultChecker.IF_ERROR_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, List<IAssinadorBase64ArquivoAssinado> files) throws PJeClientException {
    final Supplier<HttpPost> supplier = () -> createJsonPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(files, "files null")
    );
    post(supplier, ResultChecker.IF_NOT_SUCCESS_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, String certificateChain64) throws PJeClientException {
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireText(certificateChain64, "certificateChain64 empty")
    );
    post(supplier, ResultChecker.IF_NOT_SUCCESS_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, IDadosSSO dadosSSO) throws PJeClientException {
    final Supplier<HttpPost> supplier = () -> createJsonPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireNonNull(session, "session is null"), //single sign on has empty string session but not null
      requireText(userAgent, "userAgent null"), 
      requireNonNull(dadosSSO , "dadosSSO is empty")
    );
    post(supplier, ResultChecker.QUIETLY);
  }  

  private void post(final Supplier<HttpPost> supplier, Runnable<String, PJeClientException> checkResults) throws PJeClientException {
    try {
      final HttpPost postRequest = supplier.get();

      try(CloseableHttpResponse response = client.execute(postRequest)) {
        int code = response.getCode();
        if (!isSuccess(code)) { 
          throw new PJeClientException("Servidor retornando status code: " + code);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String responseText;
          try {
            responseText = EntityUtils.toString(entity, Constants.DEFAULT_CHARSET);
          } catch (ParseException | IOException e) {
            throw new PJeClientException(e);
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
      final OutputStream output = status.onNewTry(1);

      final HttpGet get = supplier.get();

      try(CloseableHttpResponse response = client.execute(get)) {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
          throw new PJeClientException("Servidor não foi capaz de retornar dados. (entity is null) - HTTP Code: " + response.getCode());
        }
        try {
          final long total = entity.getContentLength();
          final InputStream input = entity.getContent();
          status.onStartDownload(total);
          final byte[] buffer = new byte[32 * 1024];
          status.onStatus(total, 0);
          for(int length, written = 0; (length = input.read(buffer)) > 0; status.onStatus(total, written += length))
            output.write(buffer, 0, length);
          status.onEndDownload();
        } catch(Exception e) {
          status.onDownloadFail(e);
          throw new PJeClientException("Falha durante o download do arquivo", e);
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
  
  
  private static enum ResultChecker implements Runnable<String, PJeClientException> {
    
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
        throw new PJeClientException("Servidor não recebeu arquivo enviado");
      }
    }, 
    
    QUIETLY() {
      @Override
      public void run(String response) throws PJeClientException {
      }
    };
    
    private static final String SERVER_SUCCESS_RESPONSE = "Sucesso";
    private static final String SERVER_FAIL_RESPONSE    = "Erro:"; 
  }
}

