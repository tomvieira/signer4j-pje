package br.jus.cnj.pje.office.core.imp;

import static com.github.signer4j.imp.Strings.optional;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.imp.Params;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.core.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.core.IAssinadorHashParams;

class PjeAssinadorHashReader extends AbstractRequestReader<Params, PjeAssinadorHashReader.TarefaAssinadorHash>{

  public static final PjeAssinadorHashReader INSTANCE = new PjeAssinadorHashReader();

  public PjeAssinadorHashReader() {
    super(TarefaAssinadorHash.class);
  }
  
  static final class TarefaAssinadorHash implements IAssinadorHashParams {
    private boolean modoTeste;
    private boolean deslogarKeyStore = true;

    private String uploadUrl;
    private String algoritmoAssinatura;
    
    private List<IAssinadorHashArquivo> arquivos = new ArrayList<>();

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

  @Override
  protected ITask<?> createTask(Params output, TarefaAssinadorHash pojo) throws IOException {
    return new PjeAssinadorHashTask(output, pojo);
  }

}
