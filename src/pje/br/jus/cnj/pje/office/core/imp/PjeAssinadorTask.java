package br.jus.cnj.pje.office.core.imp;

import static br.jus.cnj.pje.office.core.IAssinadorParams.PJE_TAREFA_ASSINADOR_PARAM;
import static br.jus.cnj.pje.office.core.imp.PjeTaskChecker.checkIfPresent;
import static com.github.signer4j.gui.alert.MessageAlert.display;
import static com.github.signer4j.imp.SwingTools.invokeLater;

import java.io.IOException;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.imp.Params;
import com.github.signer4j.imp.TemporaryException;
import com.github.signer4j.imp.exception.KeyStoreAccessException;
import com.github.signer4j.imp.exception.RuntimeKeyStoreException;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.progress.imp.InterruptedProgress;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;

import br.jus.cnj.pje.office.core.IArquivoAssinado;
import br.jus.cnj.pje.office.core.IAssinadorParams;
import br.jus.cnj.pje.office.core.IPjeSignerMode;
import br.jus.cnj.pje.office.core.IStandardSignature;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.web.IPjeResponse;

abstract class PjeAssinadorTask extends PjeAbstractTask {

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
  
  protected IPjeSignerMode modo;

  private IStandardSignature padraoAssinatura;
  
  public PjeAssinadorTask(Params request, IAssinadorParams pojo) {
    super(request.of(PJE_TAREFA_ASSINADOR_PARAM, pojo));
  }
  
  protected final IAssinadorParams getAssinadorParams() {
    return getParameterValue(PJE_TAREFA_ASSINADOR_PARAM);
  }
  
  @Override
  protected void validateParams() throws TaskException {
    super.validateParams();
    IAssinadorParams params = getAssinadorParams();
    this.modo = checkIfPresent(params.getModo(), "modo");
    this.padraoAssinatura = checkIfPresent(params.getPadraoAssinatura(), "padraoAssinatura").checkIfDependentParamsIsPresent(params);
  }
  
  @Override
  protected final ITaskResponse<IPjeResponse> doGet() throws TaskException {
    final IAssinadorParams params = getAssinadorParams();
    boolean fail = false;
    IProgress progress = getProgress();

    progress.begin(Stage.SELECTING_FILES);
    
    final IArquivoAssinado[] files = selectFiles();
    final int size = files.length;
    
    progress.step("Selecionados '%s' arquivo(s)", size);
    progress.end();
    
    progress.begin(Stage.PROCESSING_FILES, size);
    IPjeToken token = loginToken();
    
    boolean cancel = false;
    int index = 0;
    try {
      IByteProcessor processor = padraoAssinatura.getByteProcessor(token, params);
      
      for(final IArquivoAssinado file: files) {
        try {
          final String fileName = file.getNome().orElse(Integer.toString(index + 1));

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
          } catch (KeyStoreAccessException e) {
            String message = "Arquivo ignorado:  " + file.toString();
            LOGGER.warn(message, e);
            progress.step(message + " -> " +  e.getMessage());
            progress.end();
            if (!token.isAuthenticated()) {
              try {
                token = loginToken();
              }catch(RuntimeKeyStoreException ex) {
                ex.addSuppressed(e);
                throw new TaskException("Não foi possível recuperar autenticação do token.", ex);
              }
              processor = padraoAssinatura.getByteProcessor(token, params);
            }
            throw new TemporaryException(e);
          }
          progress.step("Enviando arquivo '%s'", fileName);
          try {
            send(file);
          }catch(TaskException e) {
            String message = "Arquivo ignorado:  " + file.toString();
            LOGGER.warn(message, e);
            progress.step(message + " -> " + e.getMessage());
            progress.end();
            throw new TemporaryException(e);
          }
          progress.end();
        }catch(TaskException e) {
          progress.abort(e);
          throw e;
        }catch(TemporaryException e) {
          fail = true;
          progress.abort(e);
          int remainder = size - index - 1;
          if (remainder > 0) {
            progress.begin(Stage.PROCESSING_FILES, remainder);
          }
        }finally {
          file.dispose();
          index++;
        }
      }
      progress.end();
    }catch(Exception e) {
      cancel = e instanceof InterruptedProgress;
      fail |= index != size;
    }finally {
      token.logout(); //params.isDeslogarKeyStore() //TODO entender esse parâmetro que vem do servidor!
      System.gc();
    }
    
    if (!fail) {
      invokeLater(() -> display("Arquivos assinados com sucesso!", "Ótimo!"));
      return PjeResponse.SUCCESS;
    }
    String detail = cancel ? "Operação cancelada pelo usuário." : "Alguns arquivos NÃO puderam ser assinados.";
    invokeLater(() -> display(detail + "\nVeja detalhes no registro de atividades."));
    return PjeResponse.FAIL;
  }
  
  protected abstract IArquivoAssinado[] selectFiles() throws TaskException;

  protected abstract void send(IArquivoAssinado arquivo) throws TaskException;
}
