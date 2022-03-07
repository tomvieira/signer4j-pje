package br.jus.cnj.pje.office.task.imp;

import java.util.List;

import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaMedia;

abstract class PjeAbstractMediaTask<T extends ITarefaMedia> extends PjeAbstractTask<T> {
  
  protected List<String> arquivos;
  
  protected PjeAbstractMediaTask(Params request, T pojo) {
    super(request, pojo, true);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaMedia pojo = getPojoParams();
    this.arquivos = PjeTaskChecker.checkIfNotEmpty(pojo.getArquivos(), "arquivos");
  }
}
