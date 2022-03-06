package br.jus.cnj.pje.office.task.imp;

import java.util.List;

import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaPdf;

abstract class PjeAbstractPdfTask<T extends ITarefaPdf> extends PjeAbstractTask<T> {
  
  protected List<String> arquivos;
  
  protected PjeAbstractPdfTask(Params request, T pojo) {
    super(request, pojo, true);
  }

  @Override
  protected void validateParams() throws TaskException {
    ITarefaPdf pojo = getPojoParams();
    this.arquivos = PjeTaskChecker.checkIfNotEmpty(pojo.getArquivos(), "arquivos");
  }
}
