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

import org.apache.hc.core5.http.ContentType;

import com.github.utils4j.imp.Base64;

import br.jus.cnj.pje.office.core.IPjeResponse;

public abstract class PjeWebTaskResponse extends PjeTaskResponse {
  
  public static PjeWebTaskResponse success(boolean json) {
    return json ? JSON_SUCCESS : IMAGE_SUCCESS;
  }
  
  public static PjeWebTaskResponse fail(boolean json) {
    return json ? JSON_FAIL: IMAGE_FAIL;
  }

  //A simple json   {"success": true}
  private static final PjeWebTaskResponse JSON_SUCCESS = new PjeWebTaskResponse(true, "eyJzdWNjZXNzIjogdHJ1ZX0=") {
    @Override
    public void processResponse(IPjeResponse response) throws IOException {
      response.setContentType(ContentType.APPLICATION_JSON.toString());
      response.write(content);
    }
  };
  
  //A simple json   {"success": false}
  private static final PjeWebTaskResponse JSON_FAIL = new PjeWebTaskResponse(false, "eyJzdWNjZXNzIjogZmFsc2V9") {
    @Override
    public void processResponse(IPjeResponse response) throws IOException {
      response.setContentType(ContentType.APPLICATION_JSON.toString());
      response.write(content);
    }
  };
  
  //A .gif file with 1 pixels
  private static final PjeWebTaskResponse IMAGE_SUCCESS = new PjeWebTaskResponse(true, "R0lGODlhAQABAPAAAEz/AAAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==") {
    @Override
    public void processResponse(IPjeResponse response) throws IOException {
      response.setContentType(ContentType.IMAGE_GIF.toString());
      response.write(content);
    }
  };
  
  //A .png file with 2 pixels
  private static final PjeWebTaskResponse IMAGE_FAIL = new PjeWebTaskResponse(false, "iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAABHNCSVQICAgIfAhkiAAAABFJREFUCJlj/M/A8J+BgYEBAA0FAgD+6nhnAAAAAElFTkSuQmCC") {
    @Override
    public void processResponse(IPjeResponse response) throws IOException {
      response.setContentType(ContentType.IMAGE_PNG.toString());
      response.write(content);
    }
  };

  protected final byte[] content;
  
  private PjeWebTaskResponse(boolean success, String base64Content) {
    super(success);
    this.content = Base64.base64Decode(base64Content);
  }
}
