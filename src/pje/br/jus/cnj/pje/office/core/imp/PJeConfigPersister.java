package br.jus.cnj.pje.office.core.imp;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.github.signer4j.imp.ConfigPersister;
import com.github.signer4j.imp.Exec;
import com.github.signer4j.imp.SignerConfig;
import com.github.signer4j.imp.Strings;

import br.jus.cnj.pje.office.core.IPjeConfigPersister;
import br.jus.cnj.pje.office.core.IPjeServerAccess;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;

class PJeConfigPersister extends ConfigPersister implements IPjeConfigPersister {
  
  private static final String SERVER_LIST      = "list.server";
  
  private static final String AUTH_STRATEGY    = "auth.strategy";
  
  private static class PjeConfig extends SignerConfig {
    PjeConfig() {
      super("pjeoffice-pro");
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
  public void loadServerAccess(Exec<IPjeServerAccess> add) {
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
      add.exec(PjeServerAccess.fromString(members));
    }
  }
}
