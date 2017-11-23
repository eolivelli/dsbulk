/*
 * Copyright DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.dsbulk.connectors.cql;

import static io.reactivex.BackpressureStrategy.BUFFER;

import com.datastax.driver.core.Statement;
import io.reactivex.Flowable;
import java.io.IOException;
import java.io.Reader;

/**
 * A {@link CqlScriptReader} that exposes <a
 * href="https://github.com/ReactiveX/RxJava/wiki">RxJava</a> types for easy consumption by clients
 * using this library.
 */
public class RxJavaCqlScriptReader extends AbstractReactiveCqlScriptReader {

  /**
   * Creates a new instance in single-line mode.
   *
   * @param in the script to read.
   */
  public RxJavaCqlScriptReader(Reader in) {
    super(in);
  }

  /**
   * Creates a new instance.
   *
   * @param in the script to read.
   * @param multiLine whether to use multi-line mode or not.
   */
  public RxJavaCqlScriptReader(Reader in, boolean multiLine) {
    super(in, multiLine);
  }

  /**
   * Creates a new instance.
   *
   * @param in the script to read.
   * @param multiLine whether to use multi-line mode or not.
   * @param size the size of the buffer.
   */
  public RxJavaCqlScriptReader(Reader in, boolean multiLine, int size) {
    super(in, multiLine, size);
  }

  @Override
  public Flowable<Statement> readReactive() {
    return Flowable.create(
        e -> {
          Statement nextStatement;
          try {
            while ((nextStatement = readStatement()) != null) {
              e.onNext(nextStatement);
            }
            e.onComplete();
          } catch (IOException ex) {
            e.onError(ex);
          }
        },
        BUFFER);
  }
}
