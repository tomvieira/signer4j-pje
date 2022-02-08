package br.jus.cnj.pje.office.task;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.IContentType;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.signer4j.IPjeToken;

public interface IAssinaturaPadrao extends IContentType {
  IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params);

  IAssinaturaPadrao checkIfDependentParamsIsPresent(ITarefaAssinador params) throws TaskException ;
}
