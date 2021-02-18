package br.jus.cnj.pje.office.core.imp;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.signer4j.IHashAlgorithm;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.imp.HashAlgorithm;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.signer4j.task.exception.TaskException;

final class PjeTaskChecker {
  
  private PjeTaskChecker() {}
  
  public static void throwIf(Supplier<Boolean> predicate, String message, Object... extra) throws TaskException {
    throwIf(predicate == null || predicate.get(), message, extra);
  }
  
  public static void throwIf(boolean condition, String message, Object... extra) throws TaskException {
    if (condition) {
      throw new TaskException(format(message, extra));
    }
  }

  public static void throwIf(boolean condition, String paramName) throws TaskException {
    if (condition) {
      throw new TaskException("Parâmetro '" + paramName + "' não informado!");
    }
  }

  public static <T> T checkIfPresent(Optional<T> optional, String paramName) throws TaskException {
    throwIf(!(checkIfNull(optional, paramName)).isPresent(), "Parâmetro '" + paramName + "' não informado!");
    return optional.get();
  }
  
  public static <T> T checkIfNull(T object, String paramName) throws TaskException {
    throwIf(object == null, "Parâmetro '" + paramName + "' não informado!");
    return object;
  }
  
  
  public static ISignatureAlgorithm checkIfSupported(ISignatureAlgorithm algorithm) throws TaskException {
    throwIf(!SignatureAlgorithm.isSupported(algorithm), "Algoritmo '%' não é suportado", algorithm.getName());
    return algorithm;
  }
  
  public static IHashAlgorithm checkIfSupportedHash(IHashAlgorithm algorithm) throws TaskException {
    throwIf(!HashAlgorithm.isSupported(algorithm), "Algoritmo '%' não é suportado", algorithm.getName());
    return algorithm;
  }
  
  @SuppressWarnings("rawtypes")
  public static List checkIfNotEmpty(List<?> content, String paramName) throws TaskException {
    throwIf(content == null || content.isEmpty(), paramName);
    return content;
  }
}
