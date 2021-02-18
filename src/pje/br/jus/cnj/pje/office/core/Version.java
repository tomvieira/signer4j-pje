package br.jus.cnj.pje.office.core;

import org.apache.commons.lang3.StringUtils;

import com.github.signer4j.imp.Constants;

public enum Version {
  Version_2_0_0;
  
  private static final Version[] VALUES = Version.values();
  
  public static Version current() {
    return VALUES[VALUES.length - 1];
  }
  
  public static byte[] jsonBytes() {
    return current().toJson().getBytes(Constants.DEFAULT_CHARSET);
  }
  
  public String toJson() {
    return "{ \"versao\": \"" + toString() + "\" }";
  }
  
  @Override
  public String toString() {
    return StringUtils.replaceChars(name().substring(name().indexOf('_') + 1), '_', '.');
  }
}
