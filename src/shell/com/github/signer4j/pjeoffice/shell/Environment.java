package com.github.signer4j.pjeoffice.shell;

import static java.lang.System.getProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

final class Environment {
  
  private Environment() {}
  
  public static Optional<String> valueFrom(final String environmentVariableKey) {
    return valueFrom(environmentVariableKey, (String)null);
  }
  
  public static Optional<String> valueFrom(final String environmentVariableKey, String defauValueIfEmpty) {
    return valueFrom(environmentVariableKey, () -> defauValueIfEmpty);
  }

  public static Optional<Path> pathFrom(final String environmentVariableKey) { 
    return pathFrom(environmentVariableKey, false);
  }

  public static Optional<Path> pathFrom(final String environmentVariableKey, boolean defaultToUserHome) { 
    return pathFrom(environmentVariableKey, defaultToUserHome, false);
  }

  public static Optional<Path> pathFrom(final String environmentVariableKey, boolean defaultToUserHome, boolean mustExists) { 
    return pathFrom(environmentVariableKey, defaultToUserHome ? Paths.get(getProperty("user.home")) : null, mustExists);
  }

  public static Optional<Path> pathFrom(final String environmentVariableKey, Path defaultIfNothing) {
    return pathFrom(environmentVariableKey, defaultIfNothing, false);
  }

  public static Optional<Path> pathFrom(final String environmentVariableKey, Path defaultIfNothing, boolean mustExists) { 
    return pathFrom(environmentVariableKey, () -> defaultIfNothing, mustExists);
  }
  
  public static Optional<Path> pathFrom(final String environmentKey, Supplier<Path> defaultIfNothing) { 
    return pathFrom(environmentKey, defaultIfNothing, false);
  }
  
  public static Optional<Path> resolveTo(final String environmentVariableKey, String fileName) {
    return resolveTo(environmentVariableKey, fileName, false);
  }
  
  public static Optional<Path> resolveTo(final String environmentVariableKey, String fileName, boolean defaultToUserHome) {
    return resolveTo(environmentVariableKey, fileName, defaultToUserHome, false);
  }
  
  public static Optional<Path> resolveTo(final String environmentVariableKey, String fileName, boolean defaultToUserHome, boolean mustExists) {
    Optional<Path> basePath = pathFrom(environmentVariableKey, defaultToUserHome, mustExists);
    if (!basePath.isPresent())
      return Optional.empty();
    Path resolvedPath = basePath.get().resolve(fileName);
    if (mustExists && !resolvedPath.toFile().exists())
      return Optional.empty();
    return Optional.of(resolvedPath);
  }
    
  public static Optional<Path> pathFrom(final String environmentKey, Supplier<Path> defaultIfNothing, boolean mustExists) {
    Optional<String> environmentKeyPath = valueFrom(environmentKey);
    Path basePath = null;
    if (environmentKeyPath.isPresent()) {
      basePath = Paths.get(environmentKeyPath.get());
    } 
    
    if (basePath != null) {
      if (mustExists) {
        if (basePath.toFile().exists())
          return Optional.of(basePath);
        basePath = null;
      }
    }
    
    if (basePath == null && defaultIfNothing != null) {
      basePath = defaultIfNothing.get();
    }
    
    if (basePath == null || (mustExists && !basePath.toFile().exists())) {
      return Optional.empty();
    }

    return Optional.of(basePath);
  }
  
  public static Optional<String> valueFrom(final String environmentVariableKey, Supplier<String> defauValueIfEmpty) {
    if (environmentVariableKey == null) {
      if (defauValueIfEmpty == null)
        return Optional.empty();
      return Optional.ofNullable(defauValueIfEmpty.get());
    }
    String value = System.getenv(environmentVariableKey);
    if (value == null) {
      value = System.getProperty(environmentVariableKey);
    }
    if (value == null) {
      value = System.getProperty(environmentVariableKey.toLowerCase());
    }
    if (value == null) {
      value = System.getProperty(environmentVariableKey.toUpperCase());
    }
    if (value == null) {
      value = System.getenv(environmentVariableKey.toLowerCase());
    }
    if (value == null) {
      value = System.getenv(environmentVariableKey.toUpperCase());
    }
    if (value == null && defauValueIfEmpty != null) {
      value = defauValueIfEmpty.get();
    }
    return Optional.ofNullable(value);
  }
}
