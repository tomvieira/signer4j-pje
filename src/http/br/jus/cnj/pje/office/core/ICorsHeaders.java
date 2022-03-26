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

import org.apache.hc.core5.http.HttpHeaders;

public interface ICorsHeaders {
  String ACCESS_CONTROL_ALLOW_CREDENTIALS       = HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
  String ACCESS_CONTROL_ALLOW_HEADERS           = HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
  String ACCESS_CONTROL_ALLOW_METHODS           = HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
  String ACCESS_CONTROL_ALLOW_ORIGIN            = HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
  String ACCESS_CONTROL_EXPOSE_HEADERS          = HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
  String ACCESS_CONTROL_MAX_AGE                 = HttpHeaders.ACCESS_CONTROL_MAX_AGE;
  String ACCESS_CONTROL_REQUEST_HEADERS         = HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
  String ACCESS_CONTROL_REQUEST_METHOD          = HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
  String ACCESS_CONTROL_REQUEST_PRIVATE_NETWORK = "Access-Control-Request-Private-Network";
  String ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK   = "Access-Control-Allow-Private-Network";
}
