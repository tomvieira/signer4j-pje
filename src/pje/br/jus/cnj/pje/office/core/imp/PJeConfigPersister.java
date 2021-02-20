package br.jus.cnj.pje.office.core.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Dates;
import com.github.signer4j.imp.Exec;
import com.github.signer4j.imp.Strings;

import br.jus.cnj.pje.office.core.IFilePath;
import br.jus.cnj.pje.office.core.IPjeConfig;
import br.jus.cnj.pje.office.core.IPjeConfigPersister;
import br.jus.cnj.pje.office.core.IServerAccess;
import br.jus.cnj.pje.office.core.PjeAllowedExtensions;
import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;

public enum PJeConfigPersister implements IPjeConfigPersister {
  CONF(PjeConfig.INSTANCE);

  private static final Logger LOGGER = LoggerFactory.getLogger(PJeConfigPersister.class);
  
  private static final char LIST_DELIMITER                = '|';
  
  private static final String PJE_SERVER_LIST             = "list.server";
  
  private static final String PJE_CERTIFICATE_A1_LIST     = "list.a1";
  
  private static final String PJE_CERTIFICATE_A3_LIST     = "list.a3";

  private static final String PJE_AUTH_STRATEGY           = "auth.strategy";
  
  private static final String PJE_DEFAULT_ALIAS           = "default.certificate";
  
  private final IPjeConfig config;
  
  private PJeConfigPersister(IPjeConfig config) {
    this.config = Args.requireNonNull(config, "config is null");
  }
  
  @Override
  public void loadA1Paths(Exec<IFilePath> add) {
    load(add, PJE_CERTIFICATE_A1_LIST, PjeAllowedExtensions.PJE_CERTIFICATES);
  }
  
  @Override
  public void loadA3Paths(Exec<IFilePath> add) {
    load(add, PJE_CERTIFICATE_A3_LIST, PjeAllowedExtensions.PJE_LIBRARIES);
  }
  
  @Override
  public void saveA3Paths(IFilePath... path) {
    put(p -> "", PJE_CERTIFICATE_A3_LIST, path);    
  }
  
  @Override
  public void save(String device) {
    put(p -> "", PJE_DEFAULT_ALIAS, Strings.toArray(device));
  }
  
  @Override
  public void save(IServerAccess... access) {
    put(p -> Strings.trim(p.getProperty(PJE_SERVER_LIST, "")), PJE_SERVER_LIST, access);
  }
  
  @Override
  public void saveA1Paths(IFilePath... path) {
    put(p -> "", PJE_CERTIFICATE_A1_LIST, path);
  }
  
  @Override
  public void save(IPjeAuthStrategy strategy) {
    put(p -> "", PJE_AUTH_STRATEGY, Strings.toArray(strategy.name()));
  }
  
  @Override
  public void overwrite(IServerAccess... access) {
    put(p -> "", PJE_SERVER_LIST, access);
  }

  private void load(Exec<IFilePath> add, String param, FileNameExtensionFilter filter) {
    Properties properties = new Properties();
    if (!open(properties))
      return;
    List<String> pathList = Strings.split(properties.getProperty(param, ""), LIST_DELIMITER);
    for(String path: pathList) {
      File p = Paths.get(path).toFile();
      if (p.exists() && p.isFile() && filter.accept(p)) {
        add.exec(new PjeFilePath(p.toPath()));
      }
    }
  }
  
  @Override
  public void loadServerAccess(Exec<IServerAccess> add) {
    Properties properties = new Properties();
    if (!open(properties))
      return;
    List<String> serverList = Strings.split(properties.getProperty(PJE_SERVER_LIST, ""), LIST_DELIMITER);
    for(String server: serverList) {
      List<String> members = Strings.split(server, ';');
      if (members.size() != 4) {
        LOGGER.warn("Arquivo de configuração em formato inválido: {}", members);
        continue;
      }
      add.exec(PjeServerAccess.fromString(members));
    }
  }
 
  @Override
  public Optional<String> defaultAlias() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return Optional.ofNullable(properties.getProperty(PJE_DEFAULT_ALIAS));
  }
  
  @Override
  public Optional<String> defaultDevice() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return get(properties, 0);
  }

  @Override
  public Optional<String> defaultCertificate() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return get(properties, 1);
  }
  
  @Override
  public Optional<String> authStrategy() {
    Properties properties = new Properties();
    if (!open(properties))
      return Optional.empty();
    return Optional.ofNullable(properties.getProperty(PJE_AUTH_STRATEGY));
  }
  
  @Override
  public void delete(IServerAccess access) {
    remove(access, PJE_SERVER_LIST);
  }
  
  private boolean open(Properties properties) {
    try(FileInputStream input = new FileInputStream(config.getConfigFile())) {
      properties.load(input);
    } catch (IOException e) {
      LOGGER.warn("Não foi possível ler os arquivos de configuração", e);
      return false;
    }
    return true;
  }

  private void put(Function<Properties, String> start, String paramName, Object[] access) {
    Properties properties = new Properties();
    if(!open(properties))
      return;
    String output = start.apply(properties);
    for(Object sa: access) {
      if (sa == null)
        continue;
      output += (output.isEmpty() ? "" : LIST_DELIMITER) + sa.toString();
    }
    toDisk(properties, paramName, output);
  }
  
  private void remove(Object access, String paramName) {
    Properties properties = new Properties();
    if (!open(properties))
      return;
    String output = Strings.trim(properties.getProperty(paramName, ""));
    List<String> servers = Strings.split(output, LIST_DELIMITER);
    String accessText = access.toString();
    servers.removeIf(s -> accessText.equalsIgnoreCase(s));
    output = Strings.merge(servers, LIST_DELIMITER);
    toDisk(properties, paramName, output);
  }
  
  private void toDisk(Properties properties, String paramName, String output) {
    properties.setProperty(paramName, output);
    try(FileOutputStream out = new FileOutputStream(config.getConfigFile())) {
      properties.store(out, "Salvo em " + Dates.stringNow());
    } catch (IOException e) {
      LOGGER.warn("Não foi possível salvar o arquivo de configuração", e);
    }
  }

  private Optional<String> get(Properties properties, int index) {
    List<String> members = Strings.split(properties.getProperty(PJE_DEFAULT_ALIAS, ""), ':');
    if (members.size() != 2)
      return Optional.empty();
    return Optional.of(members.get(index));
  }
}
