package br.jus.cnj.pje.office.task.imp;

import static com.github.utils4j.IConstants.UTF_8;
import static com.github.utils4j.imp.Throwables.tryCall;
import static java.net.URLDecoder.decode;
import static java.util.stream.Collectors.toList;

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
    this.arquivos = PjeTaskChecker.checkIfNotEmpty(pojo.getArquivos(), "arquivos")
        .stream()
        .map(s -> tryCall(() -> decode(s, UTF_8.name()), s))
        .collect(toList());
  }
}
