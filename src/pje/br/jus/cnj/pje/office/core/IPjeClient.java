package br.jus.cnj.pje.office.core;

import java.io.IOException;
import java.util.List;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.ISignedData;

import br.jus.cnj.pje.office.core.imp.PjeServerException;

public interface IPjeClient extends AutoCloseable {
  
  String PJE_DEFAULT_USER_AGENT = "PjeOffice " + Version.current();
  
  void down(String endPoint, String session, String userAgent, IDownloadStatus status) throws PjeServerException;
  
  void send(String endPoint, String session, String userAgent, ISignedData signedData) throws PjeServerException;

  void send(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws PjeServerException;

  void send(String endPoint, String session, String userAgent, IArquivoAssinado file) throws PjeServerException;
  
  void send(String endPoint, String session, String userAgent, List<IAssinadorBase64ArquivoAssinado> files)  throws PjeServerException;
  
  void send(String endpointFor, String session, String userAgent, String certificateChain64) throws PjeServerException;
  
  void close() throws IOException;

}
