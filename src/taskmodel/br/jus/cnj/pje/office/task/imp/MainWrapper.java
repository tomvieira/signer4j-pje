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

import java.util.Optional;

import com.github.utils4j.imp.Args;

import br.jus.cnj.pje.office.task.IMainParams;

public abstract class MainWrapper implements IMainParams {

  private final IMainParams main;
  
  protected MainWrapper(IMainParams main) {
    this.main = Args.requireNonNull(main, "main is null");
  }
  
  @Override
  public Optional<String> getServidor() {
    return main.getServidor();
  }

  @Override
  public Optional<String> getAplicacao() {
    return main.getAplicacao();
  }

  @Override
  public Optional<String> getSessao() {
    return main.getSessao();
  }

  @Override
  public Optional<String> getCodigoSeguranca() {
    return main.getCodigoSeguranca();
  }

  @Override
  public Optional<String> getTarefaId() {
    return main.getTarefaId();
  }

  @Override
  public Optional<String> getTarefa() {
    return main.getTarefa();
  }
  
  @Override
  public Optional<String> getOrigin() {
    return main.getOrigin();
  }

}
