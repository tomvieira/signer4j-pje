package br.jus.cnj.pje.office.task.imp;

import static com.github.utils4j.imp.Strings.optional;

import java.io.IOException;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaCertChain;

class TarefaCertChainReader extends AbstractRequestReader<Params, ITarefaCertChain> {

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
  protected ITask<?> createTask(Params params, ITarefaCertChain pojo) throws IOException {
    return new PjeCertChainTask(params, pojo);
  }
}