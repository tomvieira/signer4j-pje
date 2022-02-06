package br.jus.cnj.pje.office.task.imp;

import java.io.Serializable;
import java.security.cert.CertificateException;

import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.Params;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.ITarefaAutenticador;

class PjeAutenticatorSSOTask extends PjeAutenticatorTask {

  public PjeAutenticatorSSOTask(Params request, ITarefaAutenticador pojo) {
    super(request, pojo);
  }
  
  protected String token;

  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    this.token = PjeTaskChecker.checkIfPresent(getPojoParams().getToken(), "token"); 
  }
  
  @Override
  protected void send(ISignedData signedData) throws Exception {
    getPjeClient().send(
      getEndpointFor(enviarPara), 
      getSession(), 
      getUserAgent(), 
      new DadosSSO(token, mensagem, signedData)
    );
  }
  
  private static class DadosSSO implements Serializable, IDadosSSO {

    private static final long serialVersionUID = 1L;
    
    private String uuid;
    private String mensagem;
    private String assinatura;
    private String certChain;

    public DadosSSO(String token, String mensagem, ISignedData signedData) throws CertificateException {
      super();
      this.uuid = token;
      this.mensagem = mensagem;
      this.assinatura = signedData.getSignature64();
      this.certChain = signedData.getCertificateChain64();
    }
    
    @Override
    public String getUuid() {
      return uuid;
    }

    @Override
    public String getMensagem() {
      return mensagem;
    }

    @Override
    public String getAssinatura() {
      return assinatura;
    }

    @Override
    public String getCertChain() {
      return certChain;
    }
  }
}
