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
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.task.IAssinadorBase64Arquivo;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.ITarefaAssinadorBase64;

class TarefaAssinadorBase64Reader extends AbstractRequestReader<Params, ITarefaAssinadorBase64>{

  public static final TarefaAssinadorBase64Reader INSTANCE = new TarefaAssinadorBase64Reader();

  final static class AssinadorBase64Arquivo implements IAssinadorBase64Arquivo {
    private String hashDoc;

    private String conteudoBase64;

    public AssinadorBase64Arquivo() {}

    @Override
    public Optional<String> getHashDoc() {
      return Strings.optional(this.hashDoc);
    }

    @Override
    public Optional<String> getConteudoBase64() {
      return Strings.optional(this.conteudoBase64);
    }
    
    @Override
    public void dispose() {
      hashDoc = null;
      conteudoBase64 = null;
    }
  }

  final static class AssinadorBase64ArquivoAssinado implements IAssinadorBase64ArquivoAssinado {
    private String hashDoc;
    
    private String assinaturaBase64;
    
    public AssinadorBase64ArquivoAssinado(String hashDoc, String assinaturaBase64) {
      this.hashDoc = hashDoc;
      this.assinaturaBase64 = assinaturaBase64;
    }
    
    @Override
    public String getHashDoc() {
      return this.hashDoc;
    }
    
    @Override
    public String getAssinaturaBase64() {
      return this.assinaturaBase64;
    }

    @Override
    public void dispose() {
      hashDoc = null;
      assinaturaBase64 = null;
    }
  }


  final static class TarefaAssinadorBase64 implements ITarefaAssinadorBase64 {
    private String algoritmoAssinatura;

    private String uploadUrl;

    private List<AssinadorBase64Arquivo> arquivos = new ArrayList<>();

    private boolean deslogarKeyStore = true;

    public TarefaAssinadorBase64() {}

    @Override
    public Optional<String> getAlgoritmoAssinatura() {
      return optional(algoritmoAssinatura);
    }

    @Override
    public Optional<String> getUploadUrl() {
      return optional(uploadUrl);
    }

    @Override
    public List<IAssinadorBase64Arquivo> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }

    @Override
    public boolean isDeslogarKeyStore() {
      return deslogarKeyStore;
    }
  }

  private TarefaAssinadorBase64Reader() {
    super(TarefaAssinadorBase64.class);
  }

  @Override
  protected ITask<?> createTask(Params params, ITarefaAssinadorBase64 pojo) throws IOException {
    return new PjeAssinadorBase64Task(params, pojo);
  }
}
