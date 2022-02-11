package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.AstractPjeClient.ResultChecker.QUIETLY;
import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Args.requireText;

import java.util.List;

import com.github.signer4j.IContentType;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.function.Runnable;
import com.github.signer4j.imp.function.Supplier;
import com.github.signer4j.progress.imp.ICanceller;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.IPjeTarget;

public abstract class AstractPjeClient<T> implements IPjeClient {
  
  protected static interface ResultChecker extends Runnable<String, PJeClientException> {
    static ResultChecker QUIETLY = (r) -> {};
  }
  
  protected final Version version;
  
  protected ICanceller canceller = ICanceller.NOTHING;

  private final ResultChecker ifErrorThrow;

  private final ResultChecker ifNotSuccessThrow;
  
  protected AstractPjeClient(Version version) {
    this(version, QUIETLY, QUIETLY);
  }

  protected AstractPjeClient(Version version, ResultChecker ifError, ResultChecker ifNotSuccess) {
    this.version = requireNonNull(version, "version is null");
    this.ifErrorThrow = requireNonNull(ifError, "ifError is null");
    this.ifNotSuccessThrow = requireNonNull(ifNotSuccess, "ifNotSuccess is null");
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
    return post(supplier, ResultChecker.QUIETLY);
  }  

  
  protected abstract <R extends T> R createOutput(R request, IPjeTarget target);

  protected abstract T createOutput(IPjeTarget target, Object pojo) throws Exception;

  protected abstract T createOutput(IPjeTarget target, ISignedData signedData) throws Exception;

  protected abstract T createOutput(IPjeTarget target, String certificateChain64) throws Exception;

  protected abstract T createOutput(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws Exception;

  protected abstract T createOutput(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws Exception;
  
  protected abstract PjeTaskResponse post(final Supplier<T> supplier, ResultChecker checkResults) throws PJeClientException;
}



