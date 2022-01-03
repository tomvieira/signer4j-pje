package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Strings.optional;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.core.IAssinadorBase64Arquivo;
import br.jus.cnj.pje.office.core.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.core.ITarefaAssinadorBase64;

public class TarefaAssinadorBase64Reader extends AbstractRequestReader<Params, TarefaAssinadorBase64Reader.TarefaAssinadorBase64>{

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
  protected ITask<?> createTask(Params params, TarefaAssinadorBase64 pojo) throws IOException {
    return new PjeAssinadorBase64Task(params, pojo);
  }
}
