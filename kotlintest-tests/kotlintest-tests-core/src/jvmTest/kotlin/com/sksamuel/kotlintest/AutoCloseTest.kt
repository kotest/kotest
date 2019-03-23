package com.sksamuel.kotlintest

import io.kotlintest.listener.TestListener
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.StringSpec
import java.io.Closeable

// this is here to test for github issue #294
internal object Resources

class AutoCloseTest : StringSpec() {

  private val resourceA = autoClose(Closeable2)
  private val resourceB = autoClose(Closeable1)

  init {
    "should close resources in reverse order" {
      resourceA.closed = false
      resourceB.closed = false
    }
  }
}

object AutoCloseListener : TestListener {
  override fun afterProject() {
    Closeable1.closed.shouldBeTrue()
    Closeable2.closed.shouldBeTrue()
  }
}

object Closeable1 : Closeable {

  var closed = true

  override fun close() {
    closed = true
  }
}

object Closeable2 : Closeable {

  var closed = true

  override fun close() {
    assert(Closeable1.closed)
    closed = true
  }
}
