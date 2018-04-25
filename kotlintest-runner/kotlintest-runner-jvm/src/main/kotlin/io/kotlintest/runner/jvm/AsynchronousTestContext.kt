package io.kotlintest.runner.jvm

import io.kotlintest.DefaultTestContext
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestScope
import java.util.concurrent.Phaser
import java.util.concurrent.atomic.AtomicReference

/**
 * An implementation of [TestContext] that allows asynchronous operations
 * to be executed backed by a [Phaser] for sychronization.
 *
 * This context should not be shared between [TestScope]s or different
 * executions of the same [TestCase].
 */
class AsynchronousTestContext(scope: TestScope) : DefaultTestContext(scope) {

  private val phaser = Phaser()
  private val error = AtomicReference<Throwable?>(null)

  override fun registerAsync() {
    phaser.register()
  }

  override fun arriveAsync() {
    phaser.arrive()
  }

  fun blockUntilReady() {
    registerAsync()
    phaser.arriveAndAwaitAdvance()
  }

  override fun withError(t: Throwable) {
    error.set(t)
  }

  override fun error(): Throwable? = error.get()
}