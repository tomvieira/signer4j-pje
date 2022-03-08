package br.jus.cnj.pje.office.task.imp;

import static br.jus.cnj.pje.office.task.imp.MainRequestReader.MAIN;
import static com.github.utils4j.imp.Strings.empty;
import static com.github.utils4j.imp.Strings.optional;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IJsonTranslator;
import br.jus.cnj.pje.office.task.ITarefaMedia;

/*************************************************************************************
 * Classe base para todos os manipuladores de medias (PDF's, VÍDEOS, etc)
/*************************************************************************************/

abstract class TarefaMediaReader<T extends ITarefaMedia> extends AbstractRequestReader<Params, T> implements IJsonTranslator {

  static class TarefaMedia implements ITarefaMedia {
    protected List<String> arquivos = new ArrayList<>();
    
    @Override
    public final List<String> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }

  private static List<String> flatFirst(List<String[]> params) { 
    return flatIndex(params, 0);
  }

  private static List<String> flatSecond(List<String[]> params) { 
    return flatIndex(params, 1);
  }
  
  private static List<String> flatThird(List<String[]> params) { 
    return flatIndex(params, 2);
  }
  
  private static List<String> flatFourth(List<String[]> params) { 
    return flatIndex(params, 3);
  }

  private static List<String> flatIndex(List<String[]> list, int index) {
    return list.stream()
      .map(a -> index < a.length ? a[index] : empty())
      .collect(toList());
  }
  
  private static String peekZero(List<String> flat) {
    return flat.isEmpty() ? empty() : flat.get(0);
  }
  
  private static List<String[]> arguments(Params param) {
    return param.orElse(Params.DEFAULT_KEY, Collections.<String[]>emptyList());
  }

  protected Optional<String> first(Params params) {
    List<String[]> arguments = arguments(params);
    return optional(arguments.isEmpty() ? empty() : peekZero(flatFirst(arguments)));
  }

  protected Optional<String> second(Params params) {
    List<String[]> arguments = arguments(params);
    return optional(arguments.isEmpty() ? empty() : peekZero(flatSecond(arguments)));
  }

  protected Optional<String> third(Params params) {
    List<String[]> arguments = arguments(params);
    return optional(arguments.isEmpty() ? empty() : peekZero(flatThird(arguments)));
  }
  
  protected Optional<String> fourth(Params params) {
    List<String[]> arguments = arguments(params);
    return optional(arguments.isEmpty() ? empty() : peekZero(flatFourth(arguments)));
  }
  
  protected long getLong(Params params, String message) {
    return getLong(params, this::second, message);
  }
  
  protected String getString(Params params, String message) {
    return getString(params, this::second, message);
  }
  
  protected int getInt(Params params, String message) {
    return getInt(params, this::second, message);
  }
  
  protected boolean getBoolean(Params params, String message) {
    return getBoolean(params, this::second, message);
  }

  protected long getLong(Params params, Function<Params, Optional<String>> method, String message) {
    return Args.requireLong(method.apply(params).orElse(""), message);
  }
  
  protected String getString(Params params, Function<Params, Optional<String>> method, String message) {
    return Args.requireText(method.apply(params).orElse(""), message);
  }
  
  protected int getInt(Params params, Function<Params, Optional<String>> method, String message) {
    return Args.requireInt(method.apply(params).orElse(""), message);
  }
  
  protected boolean getBoolean(Params params, Function<Params, Optional<String>> method, String message) {
    return Args.requireBoolean(method.apply(params).orElse(""), message);
  }

  protected List<String> getFiles(Params param, String message) {
    return Args.requireNonEmpty(flatFirst(arguments(param)), message);
  }

  protected TarefaMediaReader(Class<?> clazz) {
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


