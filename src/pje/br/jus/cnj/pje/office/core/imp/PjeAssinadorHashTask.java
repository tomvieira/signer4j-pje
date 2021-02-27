package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.IAssinadorHashParams.PJE_TAREFA_ASSINADOR_HASH;
import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfPresent;
import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfSupportedSig;

import java.util.List;
import java.util.Optional;

import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.ISimpleSigner;
import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.SignedData;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.core.IAssinadorHashParams;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeAssinadorHashTask extends PjeAbstractTask {

  private static enum Stage implements IStage {
    HASH_SIGNING("Assinatura de HASH's");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }

  private boolean modoTeste;

  private String uploadUrl;

  private ISignatureAlgorithm algorithm;

  private List<IAssinadorHashArquivo> arquivos;

  public PjeAssinadorHashTask(Params request, IAssinadorHashParams pojo) {
    super(request.of(PJE_TAREFA_ASSINADOR_HASH, pojo));
  }

  protected final IAssinadorHashParams getAssinadorHashParams() {
    return getParameterValue(PJE_TAREFA_ASSINADOR_HASH);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    IAssinadorHashParams params = getAssinadorHashParams();
    this.algorithm = checkIfSupportedSig(params.getAlgoritmoAssinatura(), "algoritmoAssinatura");
    this.uploadUrl = checkIfPresent(params.getUploadUrl(), "uploadUrl");
    this.modoTeste = params.isModoTeste();
    this.arquivos =  params.getArquivos();
  }

  private static byte[] hashToBytes (final String hash) {
    final int mid = hash.length() / 2;
    final byte[] b = new byte[mid];
    for (int i = 0; i < mid; i++) {
      b[i] = (byte)(Integer.parseInt(hash.substring(i << 1, (i+1) << 1), 16) & 0xFF);
    }
    return b;
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException {

    final int size = arquivos.size();
    if (size == 0) {
      throw new TaskException("Não há dados a serem assinados");
    }
    
    final String session     = getSession();
    final String userAgent   = getUserAgent();
    final String endPoint    = getEndpointFor(uploadUrl);
    final IPjeClient client  = getPjeClient();
    final IProgress progress = getProgress();
    
    boolean fail = false;
    final IPjeToken token = loginToken();
    try {
      final ISimpleSigner signer = token.signerBuilder().usingAlgorigthm(this.algorithm).build();
  
      progress.begin(Stage.HASH_SIGNING, size);
      
      int i = 0;
      do {
        try {
          final IAssinadorHashArquivo file = this.arquivos.get(i);
          final String id = checkIfPresent(file.getId(), "id");
          final String hash = checkIfPresent(file.getHash(), "hash");

          checkIfPresent(file.getCodIni(), "codIni");
          
          Optional<Long> idTarefa = file.getIdTarefa();
          if (!idTarefa.isPresent()) { //TODO ver PJeClient original que evita envio de dados com este parametro indefinido.
            progress.step("Documento Id: %s IGNORADO porque parâmetro 'idTarefa' encontra-se vazio", id);
            continue;
          }
          
          final byte[] content = hashToBytes(hash);
          
          progress.step("Documento Id: %s", id);
          
          ISignedData signedData;
          if (modoTeste) {
            signedData = SignedData.forTest();
          } else {
            try {
              signedData = signer.process(content);
            } catch (Signer4JException e) {
              throw new TaskException("Não foi possível assinar o arquivo id: " + id, e);
            }
          }
    
          try {
            client.send(endPoint, session, userAgent, signedData, file);
          } catch (PjeServerException e) {
            throw new TaskException("Não foi possível enviar o arquivo id: " + id, e);
          }
          
        }catch(Exception e) {
          fail = true;
          progress.abort(e);
          progress.begin(Stage.HASH_SIGNING, size - i - 1); //TODO eu deveria mesmo continuar?
        }
        
      }while(++i < size);
      progress.end();
    }finally {
      token.logout();
    }
    
    return fail ? PjeResponse.FAIL : PjeResponse.SUCCESS;
  }
}
