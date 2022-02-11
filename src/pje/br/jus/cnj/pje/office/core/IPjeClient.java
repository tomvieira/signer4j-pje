package br.jus.cnj.pje.office.core;

import java.io.IOException;
import java.util.List;

import com.github.signer4j.IContentType;
import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.IPjeTarget;

public interface IPjeClient extends AutoCloseable {
  
  String PJE_DEFAULT_USER_AGENT = "PjeOffice " + Version.current();
  
  void down(IPjeTarget target, IDownloadStatus status) throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, ISignedData signedData) throws PJeClientException;

  PjeTaskResponse send(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException;

  PjeTaskResponse send(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, List<IAssinadorBase64ArquivoAssinado> files)  throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, String certificateChain64) throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, IDadosSSO dadosSSO) throws PJeClientException;;
  
  void close() throws IOException;

  void setCanceller(ICanceller canceller);
}
