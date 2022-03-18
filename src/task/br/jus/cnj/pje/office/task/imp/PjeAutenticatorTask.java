package br.jus.cnj.pje.office.task.imp;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.ITarefaAutenticador;

class PjeAutenticatorTask extends PjeAbstractTask<ITarefaAutenticador> {

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
  
  public PjeAutenticatorTask(Params request, ITarefaAutenticador pojo) {
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
  protected final void onBeforeDoGet() {
    forceLogout();
  }  

  @Override
  public ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    
    final IProgress progress = getProgress();

    progress.begin(Stage.AUTHENTICATING_USER, 3);

    progress.step("Recebida a mensagem '%s'", mensagem);
    
    final byte[] content = mensagem.getBytes(IConstants.DEFAULT_CHARSET); 
    
    progress.step("Assinando o conteúdo. Algoritmo: '%s'", algorithm.getName());
    
    final ISignedData signedData;
    final IPjeToken token = loginToken();
    try {
      signedData = token.signerBuilder().usingAlgorigthm(algorithm).build().process(content);
    } catch (Signer4JException e) {
      throw progress.abort(showFail("Não foi possível assinar a mensagem.", e));
    } finally {
      token.logout();
    }
    
    progress.step("Enviando assinatura para o servidor.");
    PjeTaskResponse response;
    try {
      response = send(signedData);
    }catch(Exception e) {
      throw progress.abort(showFail("Não foi possível enviar os dados ao servidor.", e));
    }  
    progress.end();
    return response;
  }

  protected PjeTaskResponse send(ISignedData signedData) throws Exception {
    return getPjeClient().send(
      getTarget(enviarPara),
      signedData
    );
  }
}
