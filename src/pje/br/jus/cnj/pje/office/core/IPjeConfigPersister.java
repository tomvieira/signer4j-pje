package br.jus.cnj.pje.office.core;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.signer4j.IConfigPersister;

import br.jus.cnj.pje.office.signer4j.IPjeAuthStrategy;


public interface IPjeConfigPersister extends IConfigPersister {
  
  void loadServerAccess(Consumer<IPjeServerAccess> add);

  Optional<String> authStrategy(); 

  void save(IPjeServerAccess... access);

  void save(IPjeAuthStrategy strategy);
  
  void overwrite(IPjeServerAccess... access);

  void delete(IPjeServerAccess access);
}