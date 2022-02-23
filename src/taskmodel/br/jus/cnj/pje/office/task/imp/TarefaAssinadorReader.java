package br.jus.cnj.pje.office.task.imp;

import static com.github.signer4j.imp.Strings.optional;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignatureType;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.imp.SignatureType;
import com.github.signer4j.imp.Strings;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.imp.AbstractRequestReader;

import br.jus.cnj.pje.office.task.IArquivo;
import br.jus.cnj.pje.office.task.IAssinaturaPadrao;
import br.jus.cnj.pje.office.task.IPjeSignMode;
import br.jus.cnj.pje.office.task.ITarefaAssinador;

class TarefaAssinadorReader extends AbstractRequestReader<Params, ITarefaAssinador>{

  public static final TarefaAssinadorReader INSTANCE = new TarefaAssinadorReader();

  static final class TarefaAssinador implements ITarefaAssinador {

    private PjeSignMode modo;
    
    private String enviarPara;

    private boolean deslogarKeyStore = true;

    private AssinaturaPadrao padraoAssinatura = AssinaturaPadrao.NOT_ENVELOPED;
    
    private SignatureType tipoAssinatura = SignatureType.ATTACHED;
    
    private SignatureAlgorithm algoritmoHash = SignatureAlgorithm.SHA1withRSA;//HashAlgorithm.DIGEST_SHA1; TODO revisar essa inicialização
    
    private List<AssinadorArquivo> arquivos = new ArrayList<>();
   
    @Override
    public final boolean isDeslogarKeyStore() {
      return this.deslogarKeyStore;
    }
    
    @Override
    public final Optional<IPjeSignMode> getModo() {
      return ofNullable(this.modo);
    }
    
    @Override
    public final Optional<String> getEnviarPara() {
      return optional(this.enviarPara);
    }

    @Override
    public final Optional<ISignatureAlgorithm> getAlgoritmoHash() {
      return ofNullable(this.algoritmoHash);
    }

    @Override
    public final Optional<IAssinaturaPadrao> getPadraoAssinatura() {
      return ofNullable(this.padraoAssinatura);
    }

    @Override
    public final Optional<ISignatureType> getTipoAssinatura() {
      return ofNullable(this.tipoAssinatura);
    }
    
    @Override
    public final List<IArquivo> getArquivos() {
      return this.arquivos == null ? emptyList() : unmodifiableList(this.arquivos);
    }
  }

  final static class AssinadorArquivo implements IArquivo {

    public static IArquivo newInstance(File file, String prefix) {
      return new AssinadorArquivo(file, prefix);
    }
    
    private AssinadorArquivo(File file, String prefix) {
      Args.requireNonNull(file, "file is null");
      this.nome = file.getName() + "." + Strings.trim(prefix) + ".p7s";
      this.url = file.getAbsolutePath();
    }
    
    private String nome;
    private String url;
    private boolean terAtributosAssinados = true;
    private List<String> paramsEnvio = new ArrayList<>();

    private AssinadorArquivo(){}

    @Override
    public final Optional<String> getUrl() {
      return optional(this.url);
    }
    
    @Override
    public final Optional<String> getNome() {
      return optional(this.nome);
    }

    @Override
    public final boolean isTerAtributosAssinados() {
      return this.terAtributosAssinados;
    }

    @Override
    public final List<String> getParamsEnvio() {
      return this.paramsEnvio == null ? emptyList() : unmodifiableList(this.paramsEnvio);
    }
  }
  
  private TarefaAssinadorReader() {
    super(TarefaAssinador.class);
  }

  @Override
  protected ITask<?> createTask(Params output, ITarefaAssinador pojo) throws IOException {
    Optional<IPjeSignMode> mode = pojo.getModo();
    if (!mode.isPresent()) {
      throw new IOException("Parameter 'modoAssinatura' (local/remoto) not found!");
    }
    return mode.get().getTask(output, pojo);
  }
}
