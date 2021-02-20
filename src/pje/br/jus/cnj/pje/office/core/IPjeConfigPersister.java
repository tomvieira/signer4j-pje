package br.jus.cnj.pje.office.core;

import java.util.Optional;

import com.github.signer4j.imp.Exec;

import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;


public interface IPjeConfigPersister {
  Optional<String> defaultCertificate();
  
  Optional<String> defaultDevice();
  
  Optional<String> defaultAlias(); // device:certificate
  
  Optional<String> authStrategy(); // IPjeAuthStrategy.name()

  void save(String defaultAlias);  // device:certificate

  void save(IServerAccess... access);

  void save(IPjeAuthStrategy strategy);

  void saveA1Paths(IFilePath ... path);
  
  void saveA3Paths(IFilePath ... path);
  
  void loadServerAccess(Exec<IServerAccess> add);
  
  void loadA1Paths(Exec<IFilePath> add);
  
  void loadA3Paths(Exec<IFilePath> add);
  
  void overwrite(IServerAccess... access);

  void delete(IServerAccess access);

}