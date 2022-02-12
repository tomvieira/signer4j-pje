package br.jus.cnj.pje.office.core.imp;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Pair;

import br.jus.cnj.pje.office.core.IPjeContext;
import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

public final class SimpleContext implements IPjeContext {

  private final Pair<IPjeRequest, IPjeResponse> pair;
  
  public static SimpleContext of(IPjeRequest request, IPjeResponse response) {
    Args.requireNonNull(request, "request is null");
    Args.requireNonNull(response, "response is null");
    return new SimpleContext(request, response);
  }
  
  private SimpleContext(IPjeRequest request, IPjeResponse response) {    
    pair = Pair.of(request, response);    
  }

  @Override
  public IPjeRequest getRequest() {
    return pair.getKey();
  }

  @Override
  public IPjeResponse getResponse() {
    return pair.getValue();
  }  
}
