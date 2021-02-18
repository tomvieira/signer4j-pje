package br.jus.cnj.pje.office.core.imp;

import java.io.PrintStream;

public enum PjeAccessTime {
  AT_THIS_TIME("Sim desta vez"),
  AWAYS("Sempre"),
  NOT("Não"),
  NEVER("Nunca");
  
  private static final PjeAccessTime[] VALUES = values();
  
  private String message;
  
  PjeAccessTime(String message) {
    this.message = message;
  }
  
  public final String toString() {
    return message;
  }
  
  public static void printOptions(PrintStream out) {
    for(PjeAccessTime at: values()) {
      out.println("[" + (at.ordinal() + 1) + "] " + at);
    }
  }
  
  public static PjeAccessTime fromOptions(int option) {
    return option < 0 || option >= VALUES.length ? null : VALUES[option];
  }
}
