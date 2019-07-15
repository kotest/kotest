package io.kotlintest.eventually

import java.time.Duration

/**
 * A [PollInterval] represents how often Awaitility will pause before reevaluating the supplied condition.
 * <p/>
 * Note that the name "poll interval" is a bit misleading. It's actually a delay between two successive condition evaluations.
 * I.e if the condition evaluation takes 5 ms and a fixed poll interval of 100 ms is used then the next condition evaluation will happen at
 * (approximately) 105 ms. It's called <tt>PollInterval</tt> for historic reasons.
 *
 * @since 1.7.0
 */
interface PollInterval {

  /**
   * Generate the next poll interval ({@link Duration}) based on the previous {@link Duration} and/or the <code>poll count</code>.
   * The first time the poll interval is called the poll delay is used as <code>previousDuration</code>. By default the poll delay is
   * equal to {@link Duration#ZERO}.
   *
   * @param count        The number of times the condition has been polled (evaluated). Always a positive integer.
   * @return The duration of the next poll interval
   */
  fun next(count: Int): Duration
}