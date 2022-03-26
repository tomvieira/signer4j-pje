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

import java.io.IOException;
import java.nio.charset.Charset;

import com.github.utils4j.IConstants;

import br.jus.cnj.pje.office.core.IPjeResponse;

class PjeStdioTaskResponse extends PjeStringTaskResponse {

  public PjeStdioTaskResponse(String output) {
    this(output, true);
  }
  
  public PjeStdioTaskResponse(String output, boolean success) {
    this(output, IConstants.DEFAULT_CHARSET, success);
  }

  public PjeStdioTaskResponse(String output, Charset charset) {
    this(output, charset, true);
  }
  
  public PjeStdioTaskResponse(String output, Charset charset, boolean success) {
    super(output, charset, success);
  }
  
  @Override
  public void processResponse(IPjeResponse response) throws IOException {
    response.write(toBytes(output.length()));
    super.processResponse(response);
  }
  
  private static byte[] toBytes(int length) {
    byte[] bytes = new byte[4];
    bytes[0] = (byte) (length & 0xFF);
    bytes[1] = (byte) ((length >> 8) & 0xFF);
    bytes[2] = (byte) ((length >> 16) & 0xFF);
    bytes[3] = (byte) ((length >> 24) & 0xFF);
    return bytes;
  }
}
