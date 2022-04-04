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


package br.jus.cnj.pje.office.core.imp;

import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.HttpHeaders;

import com.github.signer4j.ISignedData;
import com.github.utils4j.ICanceller;
import com.github.utils4j.IContentType;
import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Supplier;

import br.jus.cnj.pje.office.core.IGetCodec;
import br.jus.cnj.pje.office.core.IPjeClient;
import br.jus.cnj.pje.office.core.IPjeHeaders;
import br.jus.cnj.pje.office.core.IResultChecker;
import br.jus.cnj.pje.office.core.ISocketCodec;
import br.jus.cnj.pje.office.core.Version;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.IPjeTarget;

public abstract class AstractPjeClient<T> implements IPjeClient {
  
  protected final Version version;
  
  protected ICanceller canceller = ICanceller.NOTHING;

  private final IResultChecker ifErrorThrow;

  private final IResultChecker ifNotSuccessThrow;
  
  private ISocketCodec<T> socket;
  
  protected AstractPjeClient(Version version, ISocketCodec<T> socket) {
    this(version, socket, IResultChecker.NOTHING, IResultChecker.NOTHING);
  }

  protected AstractPjeClient(Version version, ISocketCodec<T> socket, IResultChecker ifError, IResultChecker ifNotSuccess) {
    this.version = Args.requireNonNull(version, "version is null");
    this.ifErrorThrow = Args.requireNonNull(ifError, "ifError is null");
    this.ifNotSuccessThrow = Args.requireNonNull(ifNotSuccess, "ifNotSuccess is null");
    this.socket = Args.requireNonNull(socket, "socket is null");
  }
  
  @Override
  public final IGetCodec getCodec() {
    return socket;
  }
  
  @Override
  public final void setCanceller(ICanceller canceller) {
    if (canceller != null) {
      this.canceller = canceller;
    }
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, ISignedData signedData) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      Args.requireNonNull(target, "target is null"),
      Args.requireNonNull(signedData, "signed data null")
    );
    return post(supplier, ifErrorThrow, target.isResponseJson());
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      Args.requireNonNull(target, "target is null"), 
      Args.requireNonNull(signedData, "signedData null"),
      Args.requireNonNull(file, "file is null")
    );
    return post(supplier, ifNotSuccessThrow, target.isResponseJson());
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      Args.requireNonNull(target, "target is null"), 
      Args.requireNonNull(file, "file is null"),
      Args.requireNonNull(contentType, "contentType is null")
    );
    return post(supplier, ifErrorThrow, target.isResponseJson());
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, List<IAssinadorBase64ArquivoAssinado> files) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      Args.requireNonNull(target, "target is null"),
      Args.requireNonNull(files, "files null")
    );
    return post(supplier, ifNotSuccessThrow, target.isResponseJson());
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, String certificateChain64) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      Args.requireNonNull(target, "target is null"),
      Args.requireText(certificateChain64, "certificateChain64 empty")
    );
    return post(supplier, ifNotSuccessThrow, target.isResponseJson());
  }
  
  @Override
  public final PjeTaskResponse send(IPjeTarget target, IDadosSSO dadosSSO) throws PJeClientException {
    final Supplier<T> supplier = () -> createOutput(
      Args.requireNonNull(target, "target is null"), 
      Args.requireNonNull(dadosSSO , "dadosSSO is empty")
    );
    return post(supplier, IResultChecker.NOTHING, target.isResponseJson());
  }  
  
  @Override
  public final void down(IPjeTarget target, IDownloadStatus status) throws PJeClientException {
    Args.requireNonNull(status, "status is null");
    final Supplier<HttpGet> supplier = () -> createGet(
      Args.requireNonNull(target, "target is null")
    );
    get(supplier, status);
  }
  
  @Override
  public void close() throws Exception {
    this.socket.close();
  }

  private PjeTaskResponse post(final Supplier<T> supplier, IResultChecker checkResults, boolean json) throws PJeClientException {
    try {
      return this.socket.post(supplier, checkResults, json);
    } catch (PJeClientException e) {
      throw e;
    } catch (InterruptedException e) {
      throw new PJeClientException("Envio cancelado!", e);
    } catch(Exception e) {
      throw new PJeClientException("Falha no envio!", e);
    }
  }
  
  private void get(final Supplier<HttpGet> supplier, IDownloadStatus status) throws PJeClientException {
    try {
      this.socket.get(supplier, status);
    } catch (PJeClientException e) {
      throw e;
    } catch (InterruptedException e) {
      throw new PJeClientException("Download cancelado!", e);
    } catch(Exception e) {
      throw new PJeClientException("Falha no download!", e);
    }
  }
  
  protected final <H extends HttpUriRequestBase> H createOutput(H request, IPjeTarget target) {
    request.setHeader(HttpHeaders.COOKIE, target.getSession());
    request.setHeader(IPjeHeaders.VERSION, version.toString());    
    canceller.cancelCode(request::abort);
    return request;
  }
  
  private HttpGet createGet(IPjeTarget target) {
    return createOutput(new HttpGet(target.getEndPoint()), target);
  }
  
  protected abstract T createOutput(IPjeTarget target, Object pojo) throws Exception;

  protected abstract T createOutput(IPjeTarget target, ISignedData signedData) throws Exception;

  protected abstract T createOutput(IPjeTarget target, String certificateChain64) throws Exception;

  protected abstract T createOutput(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws Exception;

  protected abstract T createOutput(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws Exception;
}
