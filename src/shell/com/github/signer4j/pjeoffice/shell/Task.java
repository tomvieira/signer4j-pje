/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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
  },
  VIDEO_SPLIT_BY_SLICE("video.split_by_slice"),
  VIDEO_EXTRACT_AUDIO("video.extract_audio") {
    public void echo(String[] args, Properties output) {
      output.put("tipo", Strings.at(args, 2));
    }
  },
  VIDEO_CONVERT_WEBM("video.convert_webm"),
  VIDEO_OPTIMIZE("video.optimize");
  
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
