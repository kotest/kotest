package io.kotest.assertions

expect fun currentTimeMillis(): Long

fun <T> continually(durationMs: Long, f: () -> T): T? {
  val start = currentTimeMillis()
  val end = start + durationMs
  fun period() = currentTimeMillis() - start
  var times = 0
  var result: T? = null
  while (currentTimeMillis() < end) {
    try {
      result = f()
    } catch (e: AssertionError) {
      if (times == 0)
        throw e
      throw Failures.failure("Test failed after ${period()}ms; expected to pass for ${durationMs}ms; attempted $times times\nUnderlying failure was: ${e.message}",
        e)
    }
    times++
  }
  return result
}
