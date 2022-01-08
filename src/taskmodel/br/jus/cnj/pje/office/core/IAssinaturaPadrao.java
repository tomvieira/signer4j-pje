package br.jus.cnj.pje.office.core;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.signer4j.IPjeToken;

public interface IAssinaturaPadrao {
  IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params);

  IAssinaturaPadrao checkIfDependentParamsIsPresent(ITarefaAssinador params) throws TaskException ;
}
