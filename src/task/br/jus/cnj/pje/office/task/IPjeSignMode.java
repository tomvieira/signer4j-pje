package br.jus.cnj.pje.office.task;

import com.github.taskresolver4j.ITask;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;

public interface IPjeSignMode {
  ITask<IPjeResponse> getTask(Params output, ITarefaAssinador pojo);
}

