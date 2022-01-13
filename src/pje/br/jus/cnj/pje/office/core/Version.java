package br.jus.cnj.pje.office.core;

import com.github.signer4j.imp.Constants;

public enum Version {
  _1_0_24("1.0.24");
  
  private String version;

  private Version(String version) {
    this.version = version;
  }
  
  private static final Version[] VALUES = Version.values();
  
  public static Version current() {
    return VALUES[VALUES.length - 1];
  }
  
  public static byte[] jsonBytes() {
    return current().toJson().getBytes(Constants.DEFAULT_CHARSET);
  }
  
  public String toJson() {
    return "{ \"versao\": \"" + this + "\" }";
  }
  
  @Override
  public String toString() {
    return version;
  }
}
