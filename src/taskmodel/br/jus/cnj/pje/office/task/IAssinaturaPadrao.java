package br.jus.cnj.pje.office.task;

import com.github.signer4j.IByteProcessor;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.IContentType;

import br.jus.cnj.pje.office.signer4j.IPjeToken;

public interface IAssinaturaPadrao extends IContentType {
  IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params);

  IAssinaturaPadrao checkIfDependentParamsIsPresent(ITarefaAssinador params) throws TaskException ;
}
