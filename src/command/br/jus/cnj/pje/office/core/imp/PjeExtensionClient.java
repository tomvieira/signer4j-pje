package br.jus.cnj.pje.office.core.imp;

import java.io.IOException;
import java.util.List;

import com.github.signer4j.IContentType;
import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;

public class PjeExtensionClient implements IPjeClient {

  @Override
  public void down(String endPoint, String session, String userAgent, IDownloadStatus status) throws PJeClientException {
  }

  @Override
  public void send(String endPoint, String session, String userAgent, ISignedData signedData) throws PJeClientException {
  }

  @Override
  public void send(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException {
  }

  @Override
  public void send(String endPoint, String session, String userAgent, IArquivoAssinado file, IContentType extension) throws PJeClientException {
  }

  @Override
  public void send(String endPoint, String session, String userAgent, List<IAssinadorBase64ArquivoAssinado> files) throws PJeClientException {
  }

  @Override
  public void send(String endPoint, String session, String userAgent, String certificateChain64) throws PJeClientException {
  }

  @Override
  public void send(String endPoint, String session, String userAgent, IDadosSSO dadosSSO) throws PJeClientException {
  }

  @Override
  public void close() throws IOException {
  }

  @Override
  public void setCanceller(ICanceller canceller) {
    // TODO Auto-generated method stub
  }
}
