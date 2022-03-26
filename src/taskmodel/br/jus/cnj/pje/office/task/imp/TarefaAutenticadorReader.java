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

import static com.github.utils4j.imp.Strings.optional;

import java.io.IOException;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.ITarefaAutenticador;

class TarefaAutenticadorReader extends AbstractRequestReader<Params, ITarefaAutenticador>{

  public static final TarefaAutenticadorReader INSTANCE = new TarefaAutenticadorReader();

  static final class TarefaAutenticador implements ITarefaAutenticador {
    private String enviarPara;
    private String mensagem;
    private String token;
    private String algoritmoAssinatura;

    @Override
    public final Optional<String> getAlgoritmoAssinatura() {
      return optional(this.algoritmoAssinatura);
    }

    @Override
    public final Optional<String> getEnviarPara() {
      return optional(this.enviarPara);
    }

    @Override
    public final Optional<String> getMensagem() {
      return optional(this.mensagem);
    }

    @Override
    public final Optional<String> getToken() {
      return optional(this.token);
    }
  }

  protected TarefaAutenticadorReader() {
    super(TarefaAutenticador.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaAutenticador pojo) throws IOException{
    return new PjeAutenticatorTask(output, pojo);
  }
}
