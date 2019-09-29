package io.kotest.provided

import io.kotest.AbstractProjectConfig
import io.kotest.assertions.fail

object ProjectConfig : AbstractProjectConfig() {

  var start = 0L

  override fun beforeAll() {
    start = System.currentTimeMillis()
  }

  override fun parallelism(): Int = 10

  override fun afterAll() {
    val duration = System.currentTimeMillis() - start
    // if we ran in parallel the tests should take approx 2 seconds, if there is a bug
    // with parallel then they'd take around 10 seconds
    if (duration > 5000)
      fail("Parallel execution failure")
  }
}
