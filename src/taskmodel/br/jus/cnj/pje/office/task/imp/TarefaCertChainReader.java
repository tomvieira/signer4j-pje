package br.jus.cnj.pje.office.task.imp;

import static com.github.signer4j.imp.Strings.optional;

import java.io.IOException;
import java.util.Optional;

import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.task.ITarefaCertChain;
import br.jus.cnj.pje.office.task.imp.PjeCertChainTask;

class TarefaCertChainReader extends AbstractRequestReader<Params, TarefaCertChainReader.TarefaCertChain> {

  public static class TarefaCertChain implements ITarefaCertChain {
    private String uploadUrl;
    
    private boolean deslogarKeyStore = false;
    
    public TarefaCertChain() {}
    
    public Optional<String> getUploadUrl() {
      return optional(this.uploadUrl);
    }
    
    public boolean isDeslogarKeyStore() {
      return this.deslogarKeyStore;
    }
  }
  
  private TarefaCertChainReader() {
    super(TarefaCertChain.class);
  }
  
  @Override
  protected ITask<?> createTask(Params params, TarefaCertChain pojo) throws IOException {
    return new PjeCertChainTask(params, pojo);
  }
}