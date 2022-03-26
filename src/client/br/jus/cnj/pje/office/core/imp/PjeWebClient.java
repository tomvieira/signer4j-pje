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

import static com.github.utils4j.imp.Strings.trim;

import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import com.github.signer4j.ISignedData;
import com.github.utils4j.IContentType;
import com.github.utils4j.imp.Objects;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IPjeTarget;

class PjeWebClient extends AstractPjeClient<HttpPost> {

  PjeWebClient(CloseableHttpClient client, Version version) {
    super(version, new PjeWebCodec(client), Checker.IF_ERROR_THROW, Checker.IF_NOT_SUCCESS_THROW);
  }
  
  private HttpPost createPost(IPjeTarget target) {
    return createOutput(new HttpPost(target.getEndPoint()), target);
  }
  
  @Override
  protected HttpPost createOutput(IPjeTarget target, ISignedData signedData) throws Exception {
    final HttpPost postRequest = createPost(target);
    postRequest.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("assinatura", signedData.getSignature64()),
      new BasicNameValuePair("cadeiaCertificado", signedData.getCertificateChain64())
    )));
    return postRequest;
  }

  @Override
  protected HttpPost createOutput(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws Exception {
    final HttpPost postRequest = createPost(target);
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
  protected HttpPost createOutput(IPjeTarget target, IArquivoAssinado file, IContentType contentType) {
    final HttpPost postRequest = createPost(target);
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
  protected HttpPost createOutput(IPjeTarget target, String certificateChain64) throws Exception  {
    final HttpPost postRequest = createPost(target);
    postRequest.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("cadeiaDeCertificadosBase64", certificateChain64)
    )));
    return postRequest;
  }
  
  @Override
  protected HttpPost createOutput(IPjeTarget target, Object pojo) throws Exception {
    final HttpPost postRequest = createPost(target);
    postRequest.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    postRequest.setEntity(new StringEntity(Objects.toJson(pojo), ContentType.APPLICATION_JSON));
    return postRequest;
  }

  protected static enum Checker implements IResultChecker {
    
    IF_ERROR_THROW() {
      @Override
      public void run(String response) throws Exception {
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
      public void run(String response) throws Exception {
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

