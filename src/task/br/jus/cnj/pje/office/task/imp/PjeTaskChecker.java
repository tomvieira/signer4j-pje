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


package br.jus.cnj.pje.office.task.imp;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.signer4j.IHashAlgorithm;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.imp.HashAlgorithm;
import com.github.signer4j.imp.SignatureAlgorithm;
import com.github.taskresolver4j.exception.TaskException;

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
    throwIf(!(checkIfNull(optional, paramName)).isPresent(), paramName);
    return optional.get();
  }
  
  public static <T> T checkIfNull(T object, String paramName) throws TaskException {
    throwIf(object == null, paramName);
    return object;
  }
  
  public static ISignatureAlgorithm checkIfSupportedSig(Optional<String> algorithm, String paramName) throws TaskException {
    return checkIfSupportedSig(checkIfPresent(algorithm, paramName), paramName);
  }
  
  public static IHashAlgorithm checkIfSupportedHash(Optional<String> algorithm, String paramName) throws TaskException {
    return checkIfSupportedHash(checkIfPresent(algorithm, paramName), paramName);
  }

  public static ISignatureAlgorithm checkIfSupportedSig(String algorithm, String paramName) throws TaskException {
    throwIf(!SignatureAlgorithm.isSupported(algorithm), "Algoritmo '%s' não é suportado", algorithm);
    return SignatureAlgorithm.get(algorithm).get();
  }
  
  private static IHashAlgorithm checkIfSupportedHash(String algorithm, String paramName) throws TaskException {
    throwIf(!HashAlgorithm.isSupported(algorithm), "Algoritmo '%s' não é suportado", algorithm);
    return HashAlgorithm.get(algorithm).get();
  }
  
  public static <T> List<T> checkIfNotEmpty(List<T> content, String paramName) throws TaskException {
    throwIf(content == null || content.isEmpty(), paramName);
    return content;
  }
}
