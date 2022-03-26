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


package br.jus.cnj.pje.office.task.imp;

import java.util.List;
import java.util.Optional;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.task.IArquivo;

class ArquivoWrapper implements IArquivo {

  private final IArquivo arquivo;
  
  protected ArquivoWrapper(IArquivo arquivo) {
    this.arquivo = Args.requireNonNull(arquivo, "arquivo is null");
  }
  
  @Override
  public final Optional<String> getUrl() {
    return arquivo.getUrl();
  }

  @Override
  public final Optional<String> getNome() {
    return arquivo.getNome();
  }

  @Override
  public final boolean isTerAtributosAssinados() {
    return arquivo.isTerAtributosAssinados();
  }

  @Override
  public final List<String> getParamsEnvio() {
    return arquivo.getParamsEnvio();
  }
}
