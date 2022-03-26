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
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import com.github.signer4j.imp.ConfigPersister;
import com.github.signer4j.imp.SignerConfig;
import com.github.signer4j.pjeoffice.shell.ShellExtension;
import com.github.utils4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeConfigPersister;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;

class PJeConfigPersister extends ConfigPersister implements IPjeConfigPersister {
  
  private static final String SERVER_LIST      = "list.server";
  
  private static final String AUTH_STRATEGY    = "auth.strategy";
  
  private static class PjeConfig extends SignerConfig {
    PjeConfig() {
      super(ShellExtension.HOME_CONFIG_FILE.toFile());
    }
  }
  
  PJeConfigPersister() {
    super(new PjeConfig());
  }
  
  @Override
  public void save(IPjeServerAccess... access) {
    put(p -> Strings.trim(p.getProperty(SERVER_LIST, "")), SERVER_LIST, access);
  }
  
  @Override
  public void save(IPjeAuthStrategy strategy) {
    put(p -> "", AUTH_STRATEGY, Strings.toArray(strategy.name()));
  }
  
  @Override
  public void overwrite(IPjeServerAccess... access) {
    put(p -> "", SERVER_LIST, access);
  }

  @Override
  public void delete(IPjeServerAccess access) {
    remove(access, SERVER_LIST);
  }

  @Override
  public Optional<String> authStrategy() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return Optional.ofNullable(properties.getProperty(AUTH_STRATEGY));
  }

  @Override
  public void loadServerAccess(Consumer<IPjeServerAccess> add) {
    Properties properties = new Properties();
    if (!open(properties))
      return;
    List<String> serverList = Strings.split(properties.getProperty(SERVER_LIST, ""), LIST_DELIMITER);
    for(String server: serverList) {
      List<String> members = Strings.split(server, ';');
      if (members.size() != 4) {
        LOGGER.warn("Arquivo de configuração em formato inválido: {}", members);
        continue;
      }
      add.accept(PjeServerAccess.fromString(members));
    }
  }
}
