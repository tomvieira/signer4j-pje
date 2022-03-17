package com.github.signer4j.pjeoffice.shell;

import static com.github.signer4j.pjeoffice.shell.Strings.at;

import java.util.Optional;
import java.util.Properties;

enum Task {
  CNJ_ASSINADOR("cnj.assinador") {
    @Override
    public void echo(String[] args, Properties output) {
      output.put("enviarPara", at(args, 2));
      output.put("modo", at(args, 3));
      output.put("padraoAssinatura", at(args, 4));
      output.put("tipoAssinatura", at(args, 5));
      output.put("algoritmoHash", at(args, 6));
    }
  },
  PDF_JOIN("pdf.join"),
  PDF_SPLIT_BY_SIZE("pdf.split_by_size") {
    @Override
    public void echo(String[] args, Properties output) {
      output.put("tamanho", at(args, 2));
    }
  },
  PDF_SPLIT_BY_PARITY("pdf.split_by_parity") {
    @Override
    public void echo(String[] args, Properties output) {
      output.put("paridade", at(args, 2));
    }
  },
  PDF_SPLIT_BY_COUNT("pdf.split_by_count") {
    @Override
    public void echo(String[] args, Properties output) {
      output.put("totalPaginas", at(args, 2));
    }
  },
  PDF_SPLIT_BY_PATES("pdf.split_by_pages"),
  VIDEO_SPLIT_BY_DURATION("video.split_by_duration") {
    @Override
    public void echo(String[] args, Properties output) {
      output.put("duracao", at(args, 2));
    }
  },
  VIDEO_SPLIT_BY_SIZE("video.split_by_size") {
    @Override
    public void echo(String[] args, Properties output) {
      output.put("tamanho", at(args, 2));
    }
  };
  
  public static Optional<Task> from(String at) {
    for(Task task: values()) {
      if (task.getId().equalsIgnoreCase(at))
        return Optional.of(task);
    }
    return Optional.empty();
  }

  private String id;
  
  Task(String id) {
    this.id = id.toLowerCase();
  }
  
  public final String getId() {
    return id;
  }
  
  public void echo(String[] args, Properties p) { }
}
