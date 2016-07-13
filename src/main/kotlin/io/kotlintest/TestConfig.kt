package io.kotlintest

import java.util.concurrent.TimeUnit

data class TestConfig(
    val ignored: Boolean = false,
    val invocations: Int = 1,
    val timeout: Duration = Duration.unlimited,
    val threads: Int = 1,
    val tags: List<String> = listOf()) {

  @Deprecated("use the constructor with Duration instead")
  constructor(
      ignored: Boolean,
      invocations: Int,
      timeout: Long = 0,
      timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
      threads: Int,
      tags: List<String>) : this(ignored, invocations, Duration(timeout, timeoutUnit), threads, tags)
}