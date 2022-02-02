package br.jus.cnj.pje.office.task.imp;

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

import br.jus.cnj.pje.office.core.imp.PjeResponse;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.ITarefaAutenticador;
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

  protected String enviarPara;
  
  protected String mensagem;
  
  public PjeAutenticadorTask(Params request, ITarefaAutenticador pojo) {
    super(request, pojo);
  }
  
  @Override
  protected void validateParams() throws TaskException {
    final ITarefaAutenticador params = getPojoParams();
    this.enviarPara = PjeTaskChecker.checkIfPresent(params.getEnviarPara(), "enviarPara"); 
    this.mensagem = PjeTaskChecker.checkIfPresent(params.getMensagem(), "mensagem");
    this.algorithm = PjeTaskChecker.checkIfSupportedSig(params
      .getAlgoritmoAssinatura()
      .orElse(SignatureAlgorithm.MD5withRSA.getName()), 
      "algoritmoAssinatura"
    );
  }
  
  /**
   * Garante que a senha seja solicitada em todo ato de autenticação em PjeAuthenticatorTask e suas derivações 
   */
  @Override
  protected final void beforeGet() {
    getTokenAccess().logout();
  }  

  @Override
  public ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    
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
      throw progress.abort(new TaskException("Não foi possível assinar a mensagem", e));
    } finally {
      token.logout();
    }
    
    progress.step("Enviando assinatura para o servidor.");
    try {
      send(signedData);
    }catch(Exception e) {
      throw progress.abort(new TaskException("Não foi possível enviar os dados ao servidor", e));
    }  
    progress.end();
    return PjeResponse.SUCCESS;
  }

  protected void send(ISignedData signedData) throws Exception {
    getPjeClient().send(
      getEndpointFor(enviarPara), 
      getSession(), 
      getUserAgent(), 
      signedData
    );
  }
}
