package br.jus.cnj.pje.office.core;

import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;

public interface IFilePath {

  String getPath();
  
  static List<Path> toPaths(List<IFilePath> list) {
    return list.stream().map(fp -> get(fp.getPath())).collect(toList());
  }
}
