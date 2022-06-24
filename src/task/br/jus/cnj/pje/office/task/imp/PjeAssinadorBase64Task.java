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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.signer4j.IByteProcessor;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.imp.exception.InterruptedSigner4JRuntimeException;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.imp.Base64;
import com.github.utils4j.imp.Params;

import br.jus.cnj.pje.office.core.IPjeResponse;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.signer4j.IPjeToken;
import br.jus.cnj.pje.office.task.IAssinadorBase64Arquivo;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.ITarefaAssinadorBase64;
import br.jus.cnj.pje.office.task.imp.TarefaAssinadorBase64Reader.AssinadorBase64ArquivoAssinado;

class PjeAssinadorBase64Task extends PjeAbstractTask<ITarefaAssinadorBase64> {

  private static enum Stage implements IStage {
    FILE_SIGNING("Assinatura de arquivos"),
    
    FILE_SENDING("Envio de arquivos");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  }
  
  private ISignatureAlgorithm algoritmoAssinatura;

  private String uploadUrl;

  private List<IAssinadorBase64Arquivo> arquivos;

  public PjeAssinadorBase64Task(Params request, ITarefaAssinadorBase64 pojo) {
    super(request, pojo);
  }

  @Override
  protected void validateTaskParams() throws TaskException {
   ITarefaAssinadorBase64 pojo = getPojoParams();
   this.algoritmoAssinatura = PjeTaskChecker.checkIfSupportedSig(pojo.getAlgoritmoAssinatura(), "algoritmoAssinatura");
   this.uploadUrl = PjeTaskChecker.checkIfPresent(pojo.getUploadUrl(), "uploadUrl");
   this.arquivos = PjeTaskChecker.checkIfNotEmpty(pojo.getArquivos(), "arquivos");
  }

  @Override
  protected ITaskResponse<IPjeResponse> doGet() throws TaskException, InterruptedException {
    
    final IProgress progress = getProgress();
    
    IPjeToken token = loginToken();
    try {
      IByteProcessor processor = token.signerBuilder().usingAlgorithm(algoritmoAssinatura).build();
          
      final int total;
      progress.begin(Stage.FILE_SIGNING, total = arquivos.size());
        
      List<IAssinadorBase64ArquivoAssinado> saida = new ArrayList<>(total);
      
      for(int i = 0; i < total; i++) {
        IAssinadorBase64Arquivo arquivo = arquivos.get(i);
        Optional<String> hashDoc = arquivo.getHashDoc();
        if (!hashDoc.isPresent()) {
          progress.step("Ignorada entrada %s", i);
          LOGGER.warn("'hashDoc' nao encontrado na lista vinda do servidor. Entrada ignorada");
          continue;
        }
        
        Optional<String> base64 = arquivo.getConteudoBase64();
        if (!base64.isPresent()) {
          progress.step("Ignorada entrada %s", i);
          LOGGER.warn("'conteudoBase64' não encontrado na lista vinda do servidor. Entrada ignorada");
          continue;
        }
        
        byte[] input = Base64.base64Decode(base64.get());
        progress.step("Assinando arquivo %s de tamanho %s", i, input.length);
        try {
          saida.add(new AssinadorBase64ArquivoAssinado(
            hashDoc.get(),
            processor.process64(input)
          ));
        } catch (Signer4JException e) {
          progress.abort(e);

          int remainder = total - i - 1;
          if (remainder > 0) {
            if (!token.isAuthenticated()) {
              try {
                token = loginToken();
              }catch(InterruptedSigner4JRuntimeException ex) {
                progress.abort(e);
                ex.addSuppressed(e);
                throw showFail("Não foi possível recuperar autenticação do token.", ex);
              }
              processor = token.signerBuilder().build();
            }
            progress.begin(Stage.FILE_SIGNING, remainder);
          }
        } finally {
          input = null;
          arquivo.dispose();
        }
      }
      progress.end();
      
      if (saida.isEmpty()) {
        throw showFail("Nenhum documento pôde ser assinado de um total de " + total);
      }
      
      progress.begin(Stage.FILE_SENDING);
      
      PjeTaskResponse response;
      
      try {
        response = getPjeClient().send(
          getTarget(uploadUrl),
          saida
        );
      }catch(Exception e) {
        throw progress.abort(showFail("Não foi possível enviar os dados ao servidor.", e));
      }finally {
        saida.forEach(IAssinadorBase64ArquivoAssinado::dispose);
        saida.clear();
        saida = null;
      }
      progress.end();
      return response;
    }finally {
      token.logout();
    }
  }
}
