/*
 * Copyright DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.dsbulk.executor.rxjava.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.driver.core.Statement;
import io.reactivex.Flowable;
import java.util.List;
import org.junit.Test;

/** */
public class RxJavaSortedStatementBatcherTest extends RxJavaStatementBatcherTest {

  @Test
  public void should_batch_by_routing_key_as_operator() throws Exception {
    assignRoutingKeys();
    RxJavaSortedStatementBatcher batcher = new RxJavaSortedStatementBatcher();
    List<Statement> statements =
        Flowable.just(stmt1, stmt2, stmt6, stmt3, stmt4, stmt5)
            .compose(batcher)
            .toList()
            .blockingGet();
    assertThat(statements)
        .usingFieldByFieldElementComparator()
        .containsExactly(batch126, batch34, stmt5);
  }

  @Test
  public void should_batch_by_routing_token_as_operator() throws Exception {
    assignRoutingTokens();
    RxJavaSortedStatementBatcher batcher = new RxJavaSortedStatementBatcher();
    List<Statement> statements =
        Flowable.just(stmt1, stmt2, stmt5, stmt6, stmt3, stmt4)
            .compose(batcher)
            .toList()
            .blockingGet();
    assertThat(statements).usingFieldByFieldElementComparator().containsExactly(batch1256, batch34);
  }
}
