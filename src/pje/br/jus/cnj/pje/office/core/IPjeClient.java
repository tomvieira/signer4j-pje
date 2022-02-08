package br.jus.cnj.pje.office.core;

import java.io.IOException;
import java.util.List;

import com.github.signer4j.IContentType;
import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;
import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;

public interface IPjeClient extends AutoCloseable {
  
  String PJE_DEFAULT_USER_AGENT = "PjeOffice " + Version.current();
  
  void down(String endPoint, String session, String userAgent, IDownloadStatus status) throws PJeClientException;
  
  void send(String endPoint, String session, String userAgent, ISignedData signedData) throws PJeClientException;

  void send(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException;

  void send(String endPoint, String session, String userAgent, IArquivoAssinado file, IContentType contentType) throws PJeClientException;
  
  void send(String endPoint, String session, String userAgent, List<IAssinadorBase64ArquivoAssinado> files)  throws PJeClientException;
  
  void send(String endPoint, String session, String userAgent, String certificateChain64) throws PJeClientException;
  
  void send(String endPoint, String session, String userAgent, IDadosSSO dadosSSO) throws PJeClientException;;
  
  void close() throws IOException;

  void setCanceller(ICanceller canceller);
}
