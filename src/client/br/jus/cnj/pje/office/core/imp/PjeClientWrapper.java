package br.jus.cnj.pje.office.core.imp;

import java.util.List;

import com.github.signer4j.IContentType;
import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Args;
import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.IPjeTarget;

class PjeClientWrapper implements IPjeClient {

  private final IPjeClient client;
  
  protected PjeClientWrapper(IPjeClient client) {
    this.client = Args.requireNonNull(client, "client is null");
  }
  
  @Override
  public void down(IPjeTarget target, IDownloadStatus status) throws PJeClientException {
    client.down(target, status);
  }

  @Override
  public PjeTaskResponse send(IPjeTarget target, ISignedData signedData) throws PJeClientException {
    return client.send(target, signedData);
  }

  @Override
  public PjeTaskResponse send(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException {
    return client.send(target,  signedData, file);
  }

  @Override
  public PjeTaskResponse send(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws PJeClientException {
    return client.send(target, file, contentType);
  }

  @Override
  public PjeTaskResponse send(IPjeTarget target, List<IAssinadorBase64ArquivoAssinado> files) throws PJeClientException {
    return client.send(target,  files);
  }

  @Override
  public PjeTaskResponse send(IPjeTarget target, String certificateChain64) throws PJeClientException {
    return client.send(target, certificateChain64);
  }

  @Override
  public PjeTaskResponse send(IPjeTarget target, IDadosSSO dadosSSO) throws PJeClientException {
    return client.send(target, dadosSSO);
  }

  @Override
  public void close() throws Exception {
    client.close();
  }

  @Override
  public void setCanceller(ICanceller canceller) {
    client.setCanceller(canceller);
  }
}
