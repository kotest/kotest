package com.sksamuel.kotest

import io.kotest.extensions.TestListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.specs.StringSpec
import java.io.Closeable

// this is here to test for github issue #294
internal object Resources

class AutoCloseTest : StringSpec() {

  private val resourceA = autoClose(AutoCloseable4)
  private val resourceB = autoClose(Closeable3)
  private val resourceC = autoClose(Closeable2)
  private val resourceD = autoClose(AutoCloseable1)

  init {
    "should close resources in reverse order" {
      resourceA.closed = false
      resourceB.closed = false
      resourceC.closed = false
      resourceD.closed = false
    }
  }
}

object AutoCloseListener : TestListener {
  override fun afterProject() {
    AutoCloseable1.closed.shouldBeTrue()
    Closeable2.closed.shouldBeTrue()
    Closeable3.closed.shouldBeTrue()
    AutoCloseable4.closed.shouldBeTrue()
  }
}

object AutoCloseable1 : AutoCloseable {

  var closed = true

  override fun close() {
    closed = true
  }
}

object Closeable2 : Closeable {

  var closed = true

  override fun close() {
    assert(AutoCloseable1.closed)
    closed = true
  }
}

object Closeable3 : Closeable {

  var closed = true

  override fun close() {
    assert(Closeable2.closed)
    closed = true
  }
}

object AutoCloseable4 : AutoCloseable {

  var closed = true

  override fun close() {
    assert(Closeable3.closed)
    closed = true
  }
}
