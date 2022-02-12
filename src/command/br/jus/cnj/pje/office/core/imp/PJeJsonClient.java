package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Strings.trim;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.json.JSONObject;

import com.github.signer4j.IContentType;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Objects;
import com.github.signer4j.imp.Pair;

import br.jus.cnj.pje.office.core.ISocketCodec;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IPjeTarget;
import br.jus.cnj.pje.office.web.IPjeHeaders;

public class PJeJsonClient extends AstractPjeClient<JSONObject> {

  PJeJsonClient(Version version, ISocketCodec<JSONObject> postCodec) {
    super(version, postCodec);
  }
  
  @Override
  protected <R extends JSONObject> R createOutput(R request, IPjeTarget target) {
    request.put(HttpHeaders.COOKIE, target.getSession());
    request.put(IPjeHeaders.VERSION, version.toString());
    request.put(HttpHeaders.USER_AGENT, target.getUserAgent());
    return request;
  }
  
  private JSONObject createOutput(IPjeTarget target) {
    JSONObject out = createOutput(new JSONObject(), target);
    out.put("endPoint", target.getEndPoint());
    return out;
  }

  @Override
  protected JSONObject createOutput(IPjeTarget target, ISignedData signedData) throws Exception {
    JSONObject out = createOutput(target);
    out.put("assinatura", signedData.getSignature64());
    out.put("cadeiaCertificado", signedData.getCertificateChain64());
    return out;
  }

  @Override
  protected JSONObject createOutput(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws Exception {
    JSONObject out = createOutput(target);
    out.put("assinatura", signedData.getSignature64());
    out.put("cadeiaCertificado", signedData.getCertificateChain64());
    out.put("id", file.getId().orElse(""));
    out.put("codIni", file.getCodIni().orElse(""));
    out.put("hash", file.getHash().get());
    if (file.getIdTarefa().isPresent())
      out.put("idTarefa", file.getIdTarefa().get().toString());
    return out;
  }

  @Override
  protected JSONObject createOutput(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws Exception {
    JSONObject out = createOutput(target);
    JSONObject body = new JSONObject();
    body.put("mimeType", contentType.getMineType());
    body.put("charset", contentType.getCharset());
    body.put("fileName", file.getNome().get() + contentType.getExtension());
    body.put("signature", file.getSignedData().get().getSignature());
    out.put(file.getFileFieldName(), body);
    file.getParamsEnvio().stream().map(param -> {
      int idx = (param = trim(param)).indexOf('=');
      return Pair.of(
        idx < 0 ? param : param.substring(idx),  
        idx < 0 ? ""    : param.substring(idx + 1)
      );
    }).forEach(nv -> out.put(nv.getKey(), nv.getValue()));
    return out;
  }
  
  @Override
  protected JSONObject createOutput(IPjeTarget target, String certificateChain64) throws Exception {
    JSONObject out = createOutput(target);
    out.put("cadeiaDeCertificadosBase64", certificateChain64);
    return out;
  }

  @Override
  protected JSONObject createOutput(IPjeTarget target, Object pojo) throws Exception {
    JSONObject out = createOutput(target);
    out.put(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    out.put("pojo", Objects.toJson(pojo));
    return out;
  }
  
  @Override
  protected JSONObject createInput(IPjeTarget target) {
    return new JSONObject(); //TODO we have to go back here! 
  }
}
