package com.github.signer4j.pjeoffice.shell;

class Strings {
  private Strings() {}
  
  public static String trim(String text) {
    return text == null ? "" : text.trim();
  }
  
  public static String at(String[] args, int index) {
    return args == null || index >= args.length ? "" : trim(args[index]);
  }
  
  public static String at(String[] args, int index, String defaultIfNothing) {
    return args == null || index >= args.length ? trim(defaultIfNothing) : trim(args[index]);
  }

}
