package br.jus.cnj.pje.office.core.imp;

import com.github.signer4j.IDownloadStatus;
import com.github.signer4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.ISocketCodec;

abstract class SocketCodec<T> implements ISocketCodec<T> {

  protected SocketCodec() {}
  
  @Override
  public final PjeTaskResponse post(final Supplier<T> supplier, IResultChecker checkResults) throws PJeClientException {
    try {
      return doPost(supplier, checkResults);
    }catch(PJeClientException e) {
      throw e;
    }catch(Exception e) {
      throw new PJeClientException("Não foi possivel enviar dados ao servidor. ", e);
    }
  }
  
  @Override
  public final void get(Supplier<T> supplier, IDownloadStatus status) throws PJeClientException {
    try {
      doGet(supplier, status);
    } catch(PJeClientException e) {
      throw e;
    } catch(Exception e) {
      throw new PJeClientException("Não foi possivel baixar dados do servidor.", e);
    }
  }
  
  protected abstract PjeTaskResponse doPost(Supplier<T> supplier, IResultChecker checkResults) throws Exception;

  protected abstract void doGet(Supplier<T> supplier, IDownloadStatus status) throws Exception;
}
