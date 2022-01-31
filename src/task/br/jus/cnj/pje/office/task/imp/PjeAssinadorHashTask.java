package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.PjeTaskChecker.checkIfPresent;

import java.util.List;

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

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.core.imp.PjeResponse;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.ITarefaAssinadorHash;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeAssinadorHashTask extends PjeAbstractTask<ITarefaAssinadorHash> {

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

  public PjeAssinadorHashTask(Params request, ITarefaAssinadorHash pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaAssinadorHash params = getPojoParams();
    this.algorithm = PjeTaskChecker.checkIfSupportedSig(params.getAlgoritmoAssinatura(), "algoritmoAssinatura");
    this.uploadUrl = PjeTaskChecker.checkIfPresent(params.getUploadUrl(), "uploadUrl");
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
          
          final String id = file.getId().orElse("[" + i + "]");
          
          progress.step("Documento Id: %s", id);
          
          ISignedData signedData;
          if (modoTeste) {
            signedData = SignedData.forTest();
          } else {
            try {
              signedData = signer.process(hashToBytes(checkIfPresent(file.getHash(), "hash")));
            } catch (Signer4JException e) {
              throw new TaskException("Não foi possível assinar o arquivo id: " + id, e);
            }
          }
    
          try {
            client.send(endPoint, session, userAgent, signedData, file);
          } catch (PJeClientException e) {
            throw new TaskException("Não foi possível enviar o arquivo id: " + id, e);
          }
          
        }catch(Exception e) {
          fail = true;
          progress.abort(e);
          int remainder = size - i - 1;
          if (remainder > 0) {
            progress.begin(Stage.HASH_SIGNING, remainder); 
          }
        }
        
      }while(++i < size);
      progress.end();
    }finally {
      token.logout();
    }
    
    return fail ? PjeResponse.FAIL : PjeResponse.SUCCESS;
  }
}
