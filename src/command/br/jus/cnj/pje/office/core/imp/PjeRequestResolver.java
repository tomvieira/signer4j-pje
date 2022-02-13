package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;

import java.io.IOException;
import java.util.Optional;

import org.apache.hc.core5.http.HttpHeaders;

import com.github.signer4j.task.IRequestResolver;
import com.github.signer4j.task.exception.TaskResolverException;

import br.jus.cnj.pje.office.core.IPjeRequest;
import br.jus.cnj.pje.office.core.IPjeResponse;

enum PjeRequestResolver implements IRequestResolver<IPjeRequest, IPjeResponse, PjeTaskRequest> {
  INSTANCE;

  @Override
  public PjeTaskRequest resolve(IPjeRequest request) throws TaskResolverException {

    Optional<String> u = request.getParameterU();
    if (!u.isPresent()) {
      throw new TaskResolverException("Parâmetro 'u' não faz parte da requisição! (browser cache issue)");
    }
	    
    Optional<String> r = request.getParameterR();
    if (!r.isPresent()) {
      throw new TaskResolverException("Unabled to resolve task with empty request 'r' param");
    }
    
    Optional<String> a = request.getUserAgent();
    
    String rValue = r.get();
    
    PjeTaskRequest tr;
    try {
      tr = (PjeTaskRequest) MAIN.read(rValue, new PjeTaskRequest().of(HttpHeaders.USER_AGENT, a));
    } catch (IOException e) {
      throw new TaskResolverException("Unabled to read 'r' request parameter: " + rValue, e);
    }
    
    StringBuilder because = new StringBuilder();
    if (!tr.isValid(because)) {
      throw new TaskResolverException("Unabled to read 'r' request parameter because: " + because);
    }
    
    return tr;
  }
}