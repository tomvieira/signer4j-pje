package br.jus.cnj.pje.office.core.imp;

import java.nio.charset.Charset;

import com.github.utils4j.IConstants;

class PjeFileWatchTaskResponse extends PjeStringTaskResponse {

  public PjeFileWatchTaskResponse(String output) {
    this(output, IConstants.DEFAULT_CHARSET);
  }
  
  public PjeFileWatchTaskResponse(String output, Charset charset) {
    this(output, charset, true);
  }
  
  public PjeFileWatchTaskResponse(String output, Charset charset, boolean success) {
    super(output, charset, success);
  }
}
