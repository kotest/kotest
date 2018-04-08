package io.kotlintest.runner.junit5

import io.kotlintest.DefaultTestContext
import io.kotlintest.TestContext
import io.kotlintest.TestScope
import java.util.concurrent.Phaser
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

/**
 * An implementation of [TestContext] that allows asynchronous operations
 * to be executed backed by a [Phaser].
 *
 * This context should not be shared between multiple [TestScope] instances.
 */
class AsynchronousTestContext(scope: TestScope) : DefaultTestContext(scope) {

  private val phaser = Phaser()
  private val error = AtomicReference<Throwable?>(null)

  fun phaser(): Phaser = phaser
  fun error(): Throwable? = error.get()

  override fun run(fn: () -> Unit) {
    phaser.register()
    thread {
      try {
        fn()
      } catch (t: Throwable) {
        error.set(t)
      }
    }
  }

  override fun arrive() {
    phaser.arrive()
  }
}