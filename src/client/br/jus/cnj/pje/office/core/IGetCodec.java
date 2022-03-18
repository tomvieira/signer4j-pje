package br.jus.cnj.pje.office.core;

import org.apache.hc.client5.http.classic.methods.HttpGet;

import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.function.Supplier;

public interface IGetCodec extends AutoCloseable {
  void get(Supplier<HttpGet> supplier, IDownloadStatus status) throws Exception;
}
