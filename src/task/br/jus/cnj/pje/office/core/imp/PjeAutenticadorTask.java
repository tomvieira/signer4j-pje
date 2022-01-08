package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfPresent;
import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfSupportedSig;

import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Constants;
import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.ITarefaAutenticador;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.web.IPjeResponse;

class PjeAutenticadorTask extends PjeAbstractTask<ITarefaAutenticador> {

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
  
  public PjeAutenticadorTask(Params request, ITarefaAutenticador pojo) {
    super(request, pojo);
  }
  
  @Override
  protected void validateParams() throws TaskException {
    final ITarefaAutenticador params = getPojoParams();
    this.enviarPara = checkIfPresent(params.getEnviarPara(), "enviarPara"); 
    this.mensagem = checkIfPresent(params.getMensagem(), "mensagem");
    this.algorithm = checkIfSupportedSig(params
      .getAlgoritmoAssinatura()
      .orElse(SignatureAlgorithm.MD5withRSA.getName()), 
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
    } catch (Signer4JException e) {
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
