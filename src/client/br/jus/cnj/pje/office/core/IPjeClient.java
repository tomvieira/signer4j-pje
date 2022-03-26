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


package br.jus.cnj.pje.office.core;

import java.util.List;

import com.github.signer4j.ISignedData;
import com.github.utils4j.ICanceller;
import com.github.utils4j.IContentType;
import com.github.utils4j.IDownloadStatus;

import br.jus.cnj.pje.office.core.imp.PJeClientException;
import br.jus.cnj.pje.office.core.imp.PjeTaskResponse;
import br.jus.cnj.pje.office.task.IArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorBase64ArquivoAssinado;
import br.jus.cnj.pje.office.task.IAssinadorHashArquivo;
import br.jus.cnj.pje.office.task.IDadosSSO;
import br.jus.cnj.pje.office.task.IPjeTarget;

public interface IPjeClient extends AutoCloseable {
  
  String PJE_DEFAULT_USER_AGENT = "PjeOffice " + Version.current();
  
  IGetCodec getCodec();
  
  void down(IPjeTarget target, IDownloadStatus status) throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, ISignedData signedData) throws PJeClientException;

  PjeTaskResponse send(IPjeTarget target, ISignedData signedData, IAssinadorHashArquivo file) throws PJeClientException;

  PjeTaskResponse send(IPjeTarget target, IArquivoAssinado file, IContentType contentType) throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, List<IAssinadorBase64ArquivoAssinado> files)  throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, String certificateChain64) throws PJeClientException;
  
  PjeTaskResponse send(IPjeTarget target, IDadosSSO dadosSSO) throws PJeClientException;;
  
  void setCanceller(ICanceller canceller);
}
