package br.jus.cnj.pje.office.task;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.IExtensionProvider;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.signer4j.IPjeToken;

public interface IAssinaturaPadrao extends IExtensionProvider {
  IByteProcessor getByteProcessor(IPjeToken token, ITarefaAssinador params);

  IAssinaturaPadrao checkIfDependentParamsIsPresent(ITarefaAssinador params) throws TaskException ;
}
