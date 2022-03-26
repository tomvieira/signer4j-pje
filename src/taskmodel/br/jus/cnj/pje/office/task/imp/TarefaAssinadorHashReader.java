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
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.imp.AbstractRequestReader;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.ITarefaAssinadorHash;

class TarefaAssinadorHashReader extends AbstractRequestReader<Params, ITarefaAssinadorHash>{

  public static final TarefaAssinadorHashReader INSTANCE = new TarefaAssinadorHashReader();

  static final class TarefaAssinadorHash implements ITarefaAssinadorHash {
    private boolean modoTeste;
    private boolean deslogarKeyStore = true;

    private String uploadUrl;
    private String algoritmoAssinatura;
    
    private List<AssinadorHashArquivo> arquivos = new ArrayList<>();

    @Override
    public final boolean isModoTeste() {
      return this.modoTeste;
    }

    @Override
    public final boolean isDeslogarKeyStore() {
      return this.deslogarKeyStore;
    }

    @Override
    public final Optional<String> getAlgoritmoAssinatura() {
      return optional(this.algoritmoAssinatura);
    }

    @Override
    public final Optional<String> getUploadUrl() {
      return optional(this.uploadUrl);
    }

    @Override
    public final List<IAssinadorHashArquivo> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }
  
  static final class AssinadorHashArquivo implements IAssinadorHashArquivo {
    private String id;
    private String codIni;
    private String hash;
    private Boolean isBin;
    private Long idTarefa;
    
    public AssinadorHashArquivo() {}

    @Override
    public Optional<String> getId() {
      return optional(this.id);
    }

    @Override
    public Optional<String> getCodIni() {
      return optional(this.codIni);
    }

    @Override
    public Optional<String> getHash() {
      return optional(this.hash);
    }

    @Override
    public Optional<Boolean> getIsBin() {
      return ofNullable(this.isBin);
    }

    @Override
    public Optional<Long> getIdTarefa() {
      return ofNullable(this.idTarefa);
    }
  }

  private TarefaAssinadorHashReader() {
    super(TarefaAssinadorHash.class);
  }
  
  @Override
  protected ITask<?> createTask(Params output, ITarefaAssinadorHash pojo) throws IOException {
    return new PjeAssinadorHashTask(output, pojo);
  }
}
