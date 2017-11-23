/*
 * Copyright DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.dsbulk.executor.rxjava;

import com.datastax.dsbulk.executor.api.BulkExecutor;
import com.datastax.dsbulk.executor.rxjava.reader.RxJavaBulkReader;
import com.datastax.dsbulk.executor.rxjava.writer.RxJavaBulkWriter;

/**
 * An execution unit for {@link RxJavaBulkWriter bulk writes} and {@link RxJavaBulkReader bulk
 * reads} that operates in reactive mode using <a
 * href="https://github.com/ReactiveX/RxJava/wiki">RxJava</a>.
 */
public interface RxJavaBulkExecutor extends RxJavaBulkWriter, RxJavaBulkReader, BulkExecutor {}
