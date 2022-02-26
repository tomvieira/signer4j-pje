package br.jus.cnj.pje.office.core.imp;

import static com.github.utils4j.imp.Args.requireNonNull;
import static com.github.utils4j.imp.Args.requireText;

import java.util.List;

import com.github.signer4j.ISignedData;
import com.github.signer4j.progress.ICanceller;
import com.github.utils4j.IContentType;
import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.ISocketCodec;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.IPjeTarget;

public abstract class AstractPjeClient<T> implements IPjeClient {
  
  protected final Version version;
  
  protected ICanceller canceller = ICanceller.NOTHING;

  private final IResultChecker ifErrorThrow;

  private final IResultChecker ifNotSuccessThrow;
  
  protected ISocketCodec<T> socket;
  
  protected AstractPjeClient(Version version, ISocketCodec<T> socket) {
    this(version, socket, IResultChecker.NOTHING, IResultChecker.NOTHING);
  }

  protected AstractPjeClient(Version version, ISocketCodec<T> socket, IResultChecker ifError, IResultChecker ifNotSuccess) {
    this.version = requireNonNull(version, "version is null");
    this.ifErrorThrow = requireNonNull(ifError, "ifError is null");
    this.ifNotSuccessThrow = requireNonNull(ifNotSuccess, "ifNotSuccess is null");
    this.socket = requireNonNull(socket, "socket is null");
  }
  
  @Override
  public final void setCanceller(ICanceller canceller) {
    if (canceller != null) {
      this.canceller = canceller;
    }
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, ISignedData signedData) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireNonNull(target, "target is null"),
      requireNonNull(signedData, "signed data null")
    );
    return post(supplier, ifErrorThrow);
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireNonNull(target, "target is null"), 
      requireNonNull(signedData, "signedData null"),
      requireNonNull(file, "file is null")
    );
    return post(supplier, ifNotSuccessThrow);
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireNonNull(target, "target is null"), 
      requireNonNull(file, "file is null"),
      requireNonNull(contentType, "contentType is null")
    );
    return post(supplier, ifErrorThrow);
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, List<IAssinadorBase64ArquivoAssinado> files) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireNonNull(target, "target is null"),
      requireNonNull(files, "files null")
    );
    return post(supplier, ifNotSuccessThrow);
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, String certificateChain64) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireNonNull(target, "target is null"),
      requireText(certificateChain64, "certificateChain64 empty")
    );
    return post(supplier, ifNotSuccessThrow);
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, IDadosSSO dadosSSO) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireNonNull(target, "target is null"), 
      requireNonNull(dadosSSO , "dadosSSO is empty")
    );
    return post(supplier, IResultChecker.NOTHING);
  }  
  
  @Override
  public final void down(IPjeTarget target, IDownloadStatus status) throws PJeClientException {
    requireNonNull(status, "status is null");
    final Supplier<T> supplier = () -> createInput(
      requireNonNull(target, "target is null")
    );
    get(supplier, status);
  }
  
  @Override
  public void close() throws Exception {
    this.socket.close();
  }

  private PjeTaskResponse post(final Supplier<T> supplier, IResultChecker checkResults) throws PJeClientException {
    try {
      return this.socket.post(supplier, checkResults);
    } catch (PJeClientException e) {
      throw e;
    } catch(Exception e) {
      throw new PJeClientException("post codec fail", e);
    }
  }
  
  private void get(final Supplier<T> supplier, IDownloadStatus status) throws PJeClientException {
    try {
      this.socket.get(supplier, status);
    } catch (PJeClientException e) {
      throw e;
    } catch(Exception e) {
      throw new PJeClientException("get codec fail", e);
    }
  }
  
  protected abstract T createInput(IPjeTarget target);
  
  protected abstract <R extends T> R createOutput(R request, IPjeTarget target);

  protected abstract T createOutput(IPjeTarget target, Object pojo) throws Exception;

  protected abstract T createOutput(IPjeTarget target, ISignedData signedData) throws Exception;

  protected abstract T createOutput(IPjeTarget target, String certificateChain64) throws Exception;

  protected abstract T createOutput(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws Exception;

  protected abstract T createOutput(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws Exception;
}



