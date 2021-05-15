package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Args.requirePositive;
import static com.github.signer4j.imp.Args.requireText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
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
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Retryable;
import com.github.signer4j.imp.Runner;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.imp.Supplier;
import com.github.signer4j.imp.TemporaryException;

import br.jus.cnj.pje.office.core.IArquivoAssinado;
import br.jus.cnj.pje.office.core.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;

class PjeClient implements IPjeClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(PjeClient.class);
  
  private static boolean isSuccess(int code) {
    return code < 400;
  }

  private final Version version;
  private final long attemptTimeout; 
  private final long attemptInterval;
  private final CloseableHttpClient client;

  PjeClient(CloseableHttpClient client, long attemptInterval, long attemptTimeout, Version version) {
    this.client = requireNonNull(client, "client is null");
    this.attemptInterval = requirePositive(attemptInterval, "attemptInterval is null");
    this.attemptTimeout = requirePositive(attemptTimeout, "attemptTimeout is null");
    this.version = requireNonNull(version, "version is null");
  }
 
  @Override
  public void close() throws IOException {
    this.client.close();
  }
  
  private <T extends BasicHttpRequest> T createRequest(T request, String session, String userAgent) {
    request.setHeader("Cookie", session);
    request.setHeader("versao", version.toString());
    request.setHeader(HttpHeaders.USER_AGENT, userAgent);
    return request;
  }

  private HttpPost createPost(String endPoint, String session, String userAgent) {
    return createRequest(new HttpPost(endPoint), session, userAgent);
  }
  
  private HttpGet createGet(String endPoint, String session, String userAgent) {
    return createRequest(new HttpGet(endPoint), session, userAgent);
  }
  
  private HttpPost createPostRequest(String endPoint, String session, String userAgent, String assinatura, String cadeiaCertificado) {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("assinatura", assinatura));
    parameters.add(new BasicNameValuePair("cadeiaCertificado", cadeiaCertificado));
    postRequest.setEntity(new UrlEncodedFormEntity(parameters));
    return postRequest;
  }

  private HttpPost createPostRequest(String endPoint, String session, String userAgent, String assinatura, String cadeiaCertificado, IAssinadorHashArquivo file) {
    final HttpPost postRequest = createPost(endPoint, session, userAgent);
    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    parameters.add(new BasicNameValuePair("assinatura", assinatura));
    parameters.add(new BasicNameValuePair("cadeiaCertificado", cadeiaCertificado));
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
  
  @Override
  public void down(String endPoint, String session, String userAgent, final IDownloadStatus status) throws PjeServerException {
    final Supplier<HttpGet> supplier = () -> createGet(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null")
    );
    get(supplier, status);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, ISignedData signedData) throws PjeServerException {
    requireNonNull(signedData, "signedData is null");
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireText(signedData.getSignature64(), "signature64 is empty"), 
      requireText(signedData.getCertificateChain64(), "chain64 is empty")
    );
    post(supplier, ResultChecker.IF_ERROR_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws PjeServerException {
    requireNonNull(signedData, "signedData is null");
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireText(signedData.getSignature64(), "signature64 is empty"), 
      requireText(signedData.getCertificateChain64(), "chain64 is empty"),
      requireNonNull(file, "file is null")
    );
    post(supplier, ResultChecker.IF_NOT_SUCCESS_THROW);
  }
  
  @Override
  public void send(String endPoint, String session, String userAgent, IArquivoAssinado file) throws PjeServerException {
    final Supplier<HttpPost> supplier = () -> createPostRequest(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(file, "file is null")
    );
    post(supplier, ResultChecker.IF_ERROR_THROW);
  }

  private void post(final Supplier<HttpPost> supplier, Runner<String, PjeServerException> checkResults)
    throws PjeServerException {
    try {
      Retryable.attempt(attemptInterval, attemptTimeout, () -> {
        final HttpPost postRequest = supplier.get();
        try(CloseableHttpResponse response = client.execute(postRequest)) {
          HttpEntity entity = response.getEntity();
          if (entity == null) {
            throw new TemporaryException("Servidor não foi capaz de retornar dados");
          }
          try {
            int code = response.getCode();
            if (!isSuccess(code)) { 
              throw new TemporaryException("Servidor retornando status code: " + code);
            }
            String responseText;
            try {
              responseText = EntityUtils.toString(entity, Constants.DEFAULT_CHARSET);
            } catch (ParseException | IOException e) {
              throw new TemporaryException(e);
            }
            checkResults.exec(responseText);
          }finally {
            EntityUtils.consumeQuietly(entity);
          }
        } catch (IOException e) {
          throw new TemporaryException("Não foi possível executar o post", e);
        } finally {
          postRequest.clear(); //help gc! :)
        }
      }); 
    }catch(PjeServerException e) {
      throw e;
    }catch(CancellationException e) {
      throw new PjeServerException("Operação cancelada. Os dados não foram enviados ao servidor.", e);
    }catch(Exception e) {
      throw new PjeServerException("Não foi possivel enviar dados ao servidor. ", e);
    }
  }
  
  private void get(final Supplier<HttpGet> supplier, final IDownloadStatus status) throws PjeServerException {
    try {
      AtomicInteger attempt = new AtomicInteger(0);
      Retryable.attempt(attemptInterval, attemptTimeout, () -> {
        final OutputStream output = status.onNewTry(attempt.incrementAndGet());
        if (output == null) {
          throw new TemporaryException("Não é possível gravar download em output nulo");
        }
        final HttpGet get = supplier.get();
       
        try(CloseableHttpResponse response = client.execute(get)) {
          HttpEntity entity = response.getEntity();
          if (entity == null) {
            throw new TemporaryException("Servidor não foi capaz de retornar dados");
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
            throw new TemporaryException("Falha durante o download do arquivo", e);
          } finally {
            EntityUtils.consumeQuietly(entity);
          }
        } catch (IOException e) {
          throw new TemporaryException("Não foi possível executar o get", e);
        } finally {
          get.clear(); //help gc!
        }
      }); 

    } catch(PjeServerException e) {
      throw e;
    } catch(CancellationException e) {
      throw new PjeServerException("Download cancelado!", e);
    } catch(Exception e) {
      throw new PjeServerException("Não foi possivel baixar dados do servidor.", e);
    }
  }
  
  
  private static enum ResultChecker implements Runner<String, PjeServerException> {
    
    IF_ERROR_THROW() {
      @Override
      public void exec(String response) throws PjeServerException {
        final int length = response.length();
        if (response.startsWith(SERVER_FAIL_RESPONSE)) { 
          String message = length > SERVER_FAIL_RESPONSE.length() ? 
            response.substring(SERVER_FAIL_RESPONSE.length()) : 
            "Desconhecido";
          throw new PjeServerException("Servidor retornou Erro: '" + message.trim() + "'");
        }
      }
    },
    
    IF_NOT_SUCCESS_THROW() {
      @Override
      public void exec(String response) throws PjeServerException {
        if (response.startsWith(SERVER_SUCCESS_RESPONSE))
          return;
        IF_ERROR_THROW.exec(response);
        throw new PjeServerException("Servidor não recebeu arquivo enviado");
      }
    };
    
    private static final String SERVER_SUCCESS_RESPONSE = "Sucesso";
    private static final String SERVER_FAIL_RESPONSE    = "Erro:"; 
  }
}

