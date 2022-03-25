package com.github.signer4j.pjeoffice.shell.reg;

import static java.lang.Character.isDigit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

import com.github.utils4j.IConstants;
import com.github.utils4j.imp.Args;

public class RegReader {
  
  private static final String HEX_MARK = "@=hex(2):";
  
  private File input;
  private Charset charset;
  
  public RegReader(File input) {
    this(input, IConstants.UTF_16LE);
  }
  
  public RegReader(File input, Charset charset) {
    this.input = Args.requireExists(input, "input does not exists");
    this.charset = Args.requireNonNull(charset, "charset is null");
  }
  
  void toText(PrintStream out) throws IOException {
    try(BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(input), charset))) {
      String line;
      while((line = b.readLine()) != null) {
        if (!line.startsWith(HEX_MARK)) {
          out.println(line);
          continue;
        }
        out.print(HEX_MARK);
        line = line.substring(HEX_MARK.length());
        StringBuilder builder = new StringBuilder();
        int chr;
        for(int i = 0; i < line.length(); i++) {
          chr = line.charAt(i);
          if (isValidRegHexChar(chr)) {
            builder.append((char)chr);
          }
        }
        while((chr = b.read()) != '[' && chr != -1) {
          if (isValidRegHexChar(chr)) {
            builder.append((char)chr);
          }
        }
        out.print(hexToText(builder.toString()));
        if (chr == '[') {
          out.print((char)chr);
          continue;
        }
        break;
      }
    }
  }
  
  void toHex(PrintStream out) throws IOException {
    try(BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(input), charset))) {
      String line;
      while((line = b.readLine()) != null) {
        if (!line.startsWith(HEX_MARK)) {
          out.println(line);
          continue;
        }
        out.print(HEX_MARK);
        line = line.substring(HEX_MARK.length());
        out.println(textToHex(line));
      }
    }
  }
  
  private static boolean isHexChar(int chr) {
    return isDigit(chr) || (chr >= 65 && chr <= 70) || (chr >= 97 && chr <= 102);
  }
  
  private static boolean isValidRegHexChar(int chr) {
    return isHexChar(chr) || chr == (int)',';
  }

  private static String hexToText(String hexFormat) {
    String[] parts = hexFormat.split("\\,");
    StringBuilder out = new StringBuilder();
    for(String part: parts) {
      int chr = Integer.parseInt(part, 16);
      if (chr != 0) {
        out.append((char)chr);
      }
    }
    return out.toString();
  }
  
  private static String textToHex(String text) {
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < text.length(); i++) {
      if (b.length() > 0)
        b.append(',');
      b.append(Integer.toHexString(text.charAt(i)));
      b.append(",00");
    }
    if (b.length() > 0)
      b.append(',');
    b.append("00,00");
    return b.toString();
  }
  
  public static void main(String[] args) throws IOException {
    RegReader r;
    r = new RegReader(new File("./install/pdf.reg.mask"), IConstants.UTF_8);
    try(PrintStream out = new PrintStream(new File("./setup/shell/pdf.reg"), IConstants.UTF_16LE.name())) {
      r.toHex(out);
    }
    r = new RegReader(new File("./install/mp4.reg.mask"), IConstants.UTF_8);
    try(PrintStream out = new PrintStream(new File("./setup/shell/mp4.reg"), IConstants.UTF_16LE.name())) {
      r.toHex(out);
    }
  }
}
