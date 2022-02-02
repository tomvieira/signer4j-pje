package br.jus.cnj.pje.office.task.imp;

import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.signer4j.imp.SwingTools.invokeLater;

import java.io.IOException;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.TemporaryException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.signer4j.imp.exception.Signer4JRuntimeException;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.imp.PjeResponse;
import br.jus.cnj.pje.office.core.imp.UnsupportedCosignException;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinaturaPadrao;
import br.jus.cnj.pje.office.task.IPjeSignMode;
import br.jus.cnj.pje.office.task.ITarefaAssinador;
import br.jus.cnj.pje.office.web.IPjeResponse;

abstract class PjeAssinadorTask extends PjeAbstractTask<ITarefaAssinador> {

  private static enum Stage implements IStage {
    
    SELECTING_FILES("Seleção de arquivos"),
    
    PROCESSING_FILES("Processamento de arquivos"),
    
    SIGN_AND_SEND("Assinatura e envio");
    
    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public String toString() {
      return message;
    }
  }
  
  protected IPjeSignMode modo;

  protected IAssinaturaPadrao padraoAssinatura;
  
  public PjeAssinadorTask(Params request, ITarefaAssinador pojo) {
    super(request, pojo);
  }
  
  @Override
  protected void validateParams() throws TaskException {
    ITarefaAssinador params = getPojoParams();
    this.modo = PjeTaskChecker.checkIfPresent(params.getModo(), "modo");
    this.padraoAssinatura = PjeTaskChecker.checkIfPresent(params.getPadraoAssinatura(), "padraoAssinatura").checkIfDependentParamsIsPresent(params);
  }
  
  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    final ITarefaAssinador params = getPojoParams();
    
    final IProgress progress = getProgress();

    int success = 0, size = -1;
    
    progress.begin(Stage.SELECTING_FILES);
    
    final IArquivoAssinado[] files = selectFiles();
    size = files.length;
    
    progress.step("Selecionados '%s' arquivo(s)", size);
    progress.end();
    
    progress.begin(Stage.PROCESSING_FILES, size);
    IPjeToken token = loginToken();
    try {
      int index = 0;
    
      IByteProcessor processor = padraoAssinatura.getByteProcessor(token, params);
      
      for(final IArquivoAssinado file: files) {
        try {
          final String fileName = file.getNome().orElse(Integer.toString(++index));

          progress.step("Processando arquivo '%s'", fileName);
          progress.begin(Stage.SIGN_AND_SEND, 2);
          progress.step("Assinando arquivo '%s'", fileName);
          
          try {
            file.sign(processor);
          } catch (IOException e) {
            String message = "Arquivo ignorado. Não foi possível ler os bytes do arquivo temporário: ";
            LOGGER.warn(message + file.toString());
            progress.step(message + e.getMessage());
            progress.end();
            throw new TemporaryException(e);
          } catch (UnsupportedCosignException e) {
            String message = "Arquivo ignorado. Co-assinatura não é suportada: ";
            LOGGER.warn(message + file.toString());
            progress.step(message + e.getMessage());
            progress.end();
            throw new TemporaryException(e);
          } catch (Signer4JException e) {
            String message = "Arquivo ignorado:  " + file.toString();
            LOGGER.warn(message, e);
            progress.step(message + " -> " +  e.getMessage());
            progress.end();
            throw new TemporaryException(e);
          }
          try {
            progress.step("Enviando arquivo '%s'", fileName);
            send(file);
            success++;
          }catch(TaskException e) {
            String message = "Arquivo ignorado:  " + file.toString();
            LOGGER.warn(message, e);
            progress.step(message + " -> " + e.getMessage());
            progress.end();
            throw new TemporaryException(e);
          }
          progress.end();
        }catch(TemporaryException e) {
          progress.abort(e);
          int remainder = size - index - 1;
          if (remainder >= 0) {
            if (!token.isAuthenticated()) {
              try {
                token = loginToken();
              }catch(Signer4JRuntimeException ex) {
                progress.abort(e);
                ex.addSuppressed(e);
                throw new TaskException("Não foi possível recuperar autenticação do token.", ex);
              }
              processor = padraoAssinatura.getByteProcessor(token, params);
            }
            progress.begin(Stage.PROCESSING_FILES, remainder);
          }
        }finally {
          file.dispose();
        }
      }
      progress.end();
    } finally {
      token.logout();
    }
    
    if (success != size) {
      invokeLater(() -> display("Alguns arquivos NÃO puderam ser assinados.\nVeja detalhes no registro de atividades."));
      return PjeResponse.FAIL;
    }
    
    invokeLater(() -> display("Arquivos assinados com sucesso!", "Ótimo!"));
    return PjeResponse.SUCCESS;
  }
  
  protected abstract IArquivoAssinado[] selectFiles() throws TaskException, InterruptedException;

  protected abstract void send(IArquivoAssinado arquivo) throws TaskException, InterruptedException;
}
