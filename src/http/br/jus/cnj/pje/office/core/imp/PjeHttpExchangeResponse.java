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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;

import com.github.utils4j.IConstants;
import com.sun.net.httpserver.HttpExchange;

import br.jus.cnj.pje.office.core.IPjeHeaders;
import br.jus.cnj.pje.office.core.IPjeHttpExchangeResponse;

@SuppressWarnings("restriction")
public class PjeHttpExchangeResponse implements IPjeHttpExchangeResponse {

  private final HttpExchange response;
  
  public PjeHttpExchangeResponse(HttpExchange response) { 
    this.response = response;
  }

  private void setContentType(String contentType) {
    response.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE, contentType);
  }
  
  @Override
  public void write(byte[] data) throws IOException {
    response.sendResponseHeaders(HttpStatus.SC_SUCCESS, data.length);
    response.getResponseBody().write(data);
    flush();
  }
  
  @Override
  public void flush() throws IOException {
    response.getResponseBody().flush();
  }  
  
  @Override
  public void write(byte[] data, String contentType) throws IOException {
    setContentType(contentType);
    write(data);
  }
  
  @Override
  public void notFound() throws IOException {
    response.sendResponseHeaders(HttpStatus.SC_NOT_FOUND, IPjeHeaders.NO_RESPONSE_BODY);    
  }

  @Override
  public void success() throws IOException {
    response.sendResponseHeaders(HttpStatus.SC_SUCCESS, IPjeHeaders.NO_RESPONSE_BODY);
  }

  /**
   * Common file format content type's
   * */
  @Override
  public void write(File file) throws IOException{
    byte[] out = Files.readAllBytes(file.toPath());
    String name = file.getName().toLowerCase();
    if (name.endsWith(".html")) {
      write(out, ContentType.TEXT_HTML.toString());
    } else if (name.endsWith(".js")) {
      write(out, ContentType.create("text/javascript", IConstants.UTF_8).toString());
    } else if (name.endsWith(".json")) {
      write(out, ContentType.APPLICATION_JSON.toString());
    } else if (name.endsWith(".pdf")){
      write(out, ContentType.APPLICATION_PDF.toString());
    } else if (name.endsWith(".bmp")) {
      write(out, ContentType.IMAGE_BMP.toString());
    } else if (name.endsWith(".gif")) {
      write(out, ContentType.IMAGE_GIF.toString());
    } else if (name.endsWith(".jpeg")) {
      write(out, ContentType.IMAGE_JPEG.toString());
    } else if (name.endsWith(".png")) {
      write(out, ContentType.IMAGE_PNG.toString());
    } else if (name.endsWith(".svg")) {
      write(out, ContentType.IMAGE_SVG.toString());
    } else if (name.endsWith(".tiff")) {
      write(out, ContentType.IMAGE_TIFF.toString());
    } else {
      write(out, ContentType.APPLICATION_OCTET_STREAM.toString());
    }
  }  
}
