package br.jus.cnj.pje.office.core;

import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;

import br.jus.cnj.pje.office.web.IPjeResponse;

public interface ISignerMode {
  ITask<IPjeResponse> getTask(Params output, IAssinadorParams pojo);
}
