package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;
import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_JOIN;
import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_COUNT;
import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_PARITY;
import static br.jus.cnj.pje.office.task.imp.PjeTaskReader.PDF_SPLIT_BY_SIZE;
import static com.github.utils4j.imp.Strings.empty;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.IJsonTranslator;
import br.jus.cnj.pje.office.task.ITarefaPdf;
import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoContagem;
import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoParidade;
import br.jus.cnj.pje.office.task.ITarefaPdfDivisaoTamanho;

/*************************************************************************************
 * Classe base para todos os manipuladores de PDF's
/*************************************************************************************/

abstract class TarefaPdfReader<T extends ITarefaPdf> extends AbstractRequestReader<Params, T> implements IJsonTranslator {

  static class TarefaPdf implements ITarefaPdf {
    protected List<String> arquivos = new ArrayList<>();
    
    @Override
    public final List<String> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }

  protected Optional<String> first(List<String[]> params) {
    return Strings.optional(params.isEmpty() ? empty() : flatFirst(params).get(0));
  }
  
  protected Optional<String> second(List<String[]> params) {
    return Strings.optional(params.isEmpty() ? empty() : flatSecond(params).get(0));
  }

  protected Optional<String> third(List<String[]> params) {
    return Strings.optional(params.isEmpty() ? empty() : flatSecond(params).get(0));
  }

  protected static List<String> flatFirst(List<String[]> params) { 
    return flatIndex(params, 0);
  }

  protected static List<String> flatSecond(List<String[]> params) { 
    return flatIndex(params, 1);
  }
  
  protected static List<String> flatThird(List<String[]> params) { 
    return flatIndex(params, 2);
  }

  private static List<String> flatIndex(List<String[]> list, int index) {
    return list.stream()
      .map(a -> index < a.length ? a[index] : empty())
      .collect(toList());
  }

  protected TarefaPdfReader(Class<?> clazz) {
    super(clazz);
  }

  @Override
  public String toJson(Params param) throws Exception {
    return MAIN.toJson(param
      .of("tarefaId", getTarefaId())
      .of("tarefa", getTarefa(param))
    );
  }

  protected abstract String getTarefaId();
  protected abstract Object getTarefa(Params input);
}


/*************************************************************************************
 * Leitor para Junção de PDFs
/*************************************************************************************/

class TarefaPdfJuncaoReader extends TarefaPdfReader<ITarefaPdf> {

  public static final TarefaPdfJuncaoReader INSTANCE = new TarefaPdfJuncaoReader();
  
  private TarefaPdfJuncaoReader() {
    super(TarefaPdf.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdf pojo) throws IOException {
    return new PjeJoinPdfTask(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_JOIN.getId();
  }
  
  @Override
  protected Object getTarefa(Params input) {
    List<String[]> arguments = input.orElse("arguments", Collections.<String[]>emptyList());
    TarefaPdf juncao = new TarefaPdf();
    juncao.arquivos = flatFirst(arguments);
    return juncao;
  }
}

/*************************************************************************************
 * Leitor para divisão de pdf por tamanho específico PDFs
/*************************************************************************************/

class TarefaPdfDivisaoTamanhoReader extends TarefaPdfReader<ITarefaPdfDivisaoTamanho> {

  public static final TarefaPdfDivisaoTamanhoReader INSTANCE = new TarefaPdfDivisaoTamanhoReader();
  
  final static class TarefaPdfDivisaoTamanho extends TarefaPdf implements ITarefaPdfDivisaoTamanho {
    private long tamanho;
    
    public final long getTamanho(){
      return tamanho;
    }
  }
  
  private TarefaPdfDivisaoTamanhoReader() {
    super(TarefaPdfDivisaoTamanho.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoTamanho pojo) throws IOException {
    return new PjeBySizePdfSplitter(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_SIZE.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    long tamanho = Strings.toLong(second(argumetns).orElse(""), -1);
    if (tamanho <= 0) {
      throw new IllegalArgumentException("'tamanho' não informado");
    }
    
    TarefaPdfDivisaoTamanho tarefaTamanho = new TarefaPdfDivisaoTamanho();
    tarefaTamanho.tamanho = tamanho;
    tarefaTamanho.arquivos = flatFirst(argumetns);
    return tarefaTamanho;
  }
}


/*************************************************************************************
 * Leitor para divisão de pdf por PARIDADE
/*************************************************************************************/

class TarefaPdfDivisaoParidadeReader extends TarefaPdfReader<ITarefaPdfDivisaoParidade> {

  public static final TarefaPdfDivisaoParidadeReader INSTANCE = new TarefaPdfDivisaoParidadeReader();
  
  final static class TarefaPdfDivisaoParidade extends TarefaPdf implements ITarefaPdfDivisaoParidade {
    private boolean par;

    @Override
    public boolean isPar() {
      return par;
    }
  }
  
  private TarefaPdfDivisaoParidadeReader() {
    super(TarefaPdfDivisaoParidade.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoParidade pojo) throws IOException {
    return new PjeByParityPdfSplitter(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_PARITY.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    String par = second(argumetns).orElse("");
    if (!Strings.isBoolean(par)) {
      throw new IllegalArgumentException("'paridade' não informado");
    }
    boolean isPar = Boolean.valueOf(par);
    TarefaPdfDivisaoParidade tarefaTamanho = new TarefaPdfDivisaoParidade();
    tarefaTamanho.par = isPar;
    tarefaTamanho.arquivos = flatFirst(argumetns);
    return tarefaTamanho;
  }
}


/*************************************************************************************
 * Leitor para divisão de pdf por contagem de páginas
/*************************************************************************************/

class TarefaPdfDivisaoContagemReader extends TarefaPdfReader<ITarefaPdfDivisaoContagem> {

  public static final TarefaPdfDivisaoContagemReader INSTANCE = new TarefaPdfDivisaoContagemReader();
  
  final static class TarefaPdfDivisaoContagem extends TarefaPdf implements ITarefaPdfDivisaoContagem {
    private long totalPaginas;

    @Override
    public long getTotalPaginas() {
      return totalPaginas;
    }
  }
  
  private TarefaPdfDivisaoContagemReader() {
    super(TarefaPdfDivisaoContagem.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaPdfDivisaoContagem pojo) throws IOException {
    return new PjeByCountPdfSplitter(output, pojo);
  }

  @Override
  protected String getTarefaId() {
    return PDF_SPLIT_BY_COUNT.getId();
  }

  @Override
  protected Object getTarefa(Params param) {
    List<String[]> argumetns = param.orElse("arguments", Collections.<String[]>emptyList());
    String total = second(argumetns).orElse("");
    if (!Strings.isLong(total)) {
      throw new IllegalArgumentException("'totalPaginas' não informado");
    }
    long totalPaginas = Long.valueOf(total);
    TarefaPdfDivisaoContagem tarefaTamanho = new TarefaPdfDivisaoContagem();
    tarefaTamanho.totalPaginas = totalPaginas;
    tarefaTamanho.arquivos = flatFirst(argumetns);
    return tarefaTamanho;
  }
}

