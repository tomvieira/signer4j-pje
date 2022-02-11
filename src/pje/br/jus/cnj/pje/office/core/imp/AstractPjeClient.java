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

abstract class AstractPjeClient<T> implements IPjeClient {
  
  static interface ResultChecker extends Runnable<String, PJeClientException> {
    static ResultChecker QUIETLY = (r) -> {};
  }
  
  protected final Version version;
  
  protected ICanceller canceller = ICanceller.NOTHING;

  private final ResultChecker ifError;

  private final ResultChecker ifNotSuccess;
  
  protected AstractPjeClient(Version version) {
    this(version, QUIETLY, QUIETLY);
  }

  protected AstractPjeClient(Version version, ResultChecker ifError, ResultChecker ifNotSuccess) {
    this.version = requireNonNull(version, "version is null");
    this.ifError = ifError;
    this.ifNotSuccess = ifNotSuccess;
  }
  
  @Override
  public final void setCanceller(ICanceller canceller) {
    if (canceller != null) {
      this.canceller = canceller;
    }
  }
  
  @Override
  public final void send(String endPoint, String session, String userAgent, ISignedData signedData) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(signedData, "signed data null")
    );
    post(supplier, ifError);
  }
  
  @Override
  public final void send(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(signedData, "signedData null"),
      requireNonNull(file, "file is null")
    );
    post(supplier, ifNotSuccess);
  }
  
  @Override
  public final void send(String endPoint, String session, String userAgent, IArquivoAssinado file, IContentType contentType) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(file, "file is null"),
      requireNonNull(contentType, "contentType is null")
    );
    post(supplier, ifError);
  }
  
  @Override
  public final void send(String endPoint, String session, String userAgent, List<IAssinadorBase64ArquivoAssinado> files) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireNonNull(files, "files null")
    );
    post(supplier, ifNotSuccess);
  }
  
  @Override
  public final void send(String endPoint, String session, String userAgent, String certificateChain64) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireText(endPoint, "empty endPoint"), 
      requireText(session, "session empty"),
      requireText(userAgent, "userAgent null"), 
      requireText(certificateChain64, "certificateChain64 empty")
    );
    post(supplier, ifNotSuccess);
  }
  
  @Override
  public final void send(String endPoint, String session, String userAgent, IDadosSSO dadosSSO) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      requireText(endPoint, "empty endPoint"), 
      requireNonNull(session, "session is null"), //single sign on has empty string session but not null
      requireText(userAgent, "userAgent null"), 
      requireNonNull(dadosSSO , "dadosSSO is empty")
    );
    post(supplier, ResultChecker.QUIETLY);
  }  

  
  protected abstract <R extends T> R createOutput(R request, String session, String userAgent);

  protected abstract T createOutput(String endPoint, String session, String userAgent, ISignedData signedData) throws Exception;

  protected abstract T createOutput(String endPoint, String session, String userAgent, ISignedData signedData, IAssinadorHashArquivo file) throws Exception;
  
  protected abstract T createOutput(String endPoint, String session, String userAgent, IArquivoAssinado file, IContentType contentType) throws Exception;
  
  protected abstract T createOutput(String endPoint, String session, String userAgent, String certificateChain64) throws Exception;
  
  protected abstract T createOutput(String endPoint, String session, String userAgent, Object pojo) throws Exception;
  
  protected abstract void post(final Supplier<T> supplier, Runnable<String, PJeClientException> checkResults) throws PJeClientException;
}



