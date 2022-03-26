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

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeServerAccess;

public class PjeServerAccess implements IPjeServerAccess {
  
  public static IPjeServerAccess fromString(List<String> members) {
    return new PjeServerAccess(
        members.get(0),
        members.get(1),
        members.get(2),
      Strings.toBoolean(members.get(3), false)
    );
  }
  
  private String id;
  private String app;
  private String server;
  private String code;
  private boolean autorized;
  
  public PjeServerAccess(String app, String server, String code) {
    this(
      Args.requireNonNull(app, "app is null"), 
      Args.requireNonNull(server, "server is null"), 
      Args.requireNonNull(code, "code is null"), 
      false
    );
  }
  
  public PjeServerAccess(String app, String server, String code, boolean autorized) {
    this.app = app;
    this.server = server;
    this.code = code;
    this.autorized = autorized;
    this.id = app.toLowerCase() + "|" + server.toLowerCase() + "|" + code.toLowerCase();
  }
  
  @Override
  public final String getId() {
    return id;
  }

  @Override
  public final String getApp() {
    return app;
  }

  @Override
  public final String getServer() {
    return server;
  }

  @Override
  public final String getCode() {
    return code;
  }
  
  @Override
  public final boolean isAutorized() {
    return this.autorized;
  }
  
  @Override
  public final String toString() {
    return String.format("%s;%s;%s;%s", app, server, code, autorized); //DO NOT REMOVE THIS TOSTRNIG FORMAT
  }
  
  @Override
  public final IPjeServerAccess clone(boolean allowed) {
    return new PjeServerAccess(app, server, code, allowed);
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PjeServerAccess other = (PjeServerAccess) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
