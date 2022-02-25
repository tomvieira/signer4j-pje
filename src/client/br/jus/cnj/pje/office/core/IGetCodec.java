package br.jus.cnj.pje.office.core;

import com.github.utils4j.IDownloadStatus;
import com.github.utils4j.imp.function.Supplier;

public interface IGetCodec<T> extends AutoCloseable {
  void get(Supplier<T> supplier, IDownloadStatus status) throws Exception;
}
