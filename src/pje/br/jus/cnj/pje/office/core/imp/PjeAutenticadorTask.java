package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.IAutenticadorParams.PJE_TAREFA_AUTENTICADOR_PARAM;
import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfPresent;
import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfSupportedSig;
import static com.github.signer4j.imp.SignatureAlgorithm.MD5withRSA;

import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.IAutenticadorParams;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeAutenticadorTask extends PjeAbstractTask {

  private static enum Stage implements IStage {
    AUTHENTICATING_USER("Autenticação de usuário");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private ISignatureAlgorithm algorithm;

  private String enviarPara;
  
  private String mensagem;
  
  public PjeAutenticadorTask(Params request, IAutenticadorParams pojo) {
    super(request.of(PJE_TAREFA_AUTENTICADOR_PARAM, pojo));
  }
  
  protected final IAutenticadorParams getAutenticadorParams() {
    return getParameterValue(PJE_TAREFA_AUTENTICADOR_PARAM);
  }

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    final IAutenticadorParams params = getAutenticadorParams();
    this.enviarPara = checkIfPresent(params.getEnviarPara(), "enviarPara"); 
    this.mensagem = checkIfPresent(params.getMensagem(), "mensagem");
    this.algorithm = checkIfSupportedSig(params
      .getAlgoritmoAssinatura()
      .orElse(MD5withRSA.getName()), 
      "algoritmoAssinatura"
    );
  }
  
  @Override
  public ITaskResponse<IPjeResponse> doGet() throws TaskException {
    
    final IProgress progress = getProgress();
    
    progress.begin(Stage.AUTHENTICATING_USER);

    progress.step("Recebida a mensagem '%s'", mensagem);
    
    final byte[] content = mensagem.getBytes(Constants.DEFAULT_CHARSET); 
    
    progress.step("Assinando o conteúdo. Algoritmo: '%s'", algorithm.getName());
    
    final ISignedData signedData;
    final IPjeToken token = loginToken();
    try {
      signedData = token.signerBuilder().usingAlgorigthm(algorithm).build().process(content);
    } catch (KeyStoreAccessException e) {
      TaskException ex = new TaskException("Não foi possível assinar a mensagem", e);
      progress.abort(ex);
      throw ex;
    } finally {
      token.logout();
    }
    
    progress.step("Enviando assinatura para o servidor.");
    final IPjeClient client = getPjeClient();
    try {
      client.send(getEndpointFor(enviarPara), getSession(), getUserAgent(), signedData);
    }catch(Exception e) {
      TaskException ex =  new TaskException("Não foi possível enviar os dados ao servidor", e);
      progress.abort(ex);
      throw ex;
    }
    
    progress.end();
    return PjeResponse.SUCCESS;
  }
}
