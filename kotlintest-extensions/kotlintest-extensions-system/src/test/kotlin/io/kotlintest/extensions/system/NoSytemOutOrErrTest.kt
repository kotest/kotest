package io.kotlintest.extensions.system

import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class NoSytemOutOrErrTest : StringSpec() {

  override fun listeners() = listOf(NoSystemOutListener, NoSystemErrListener)

  init {

    "System.out should throw an exception when the listener is added" {
      shouldThrow<SystemOutWriteException> {
        System.out.println("boom")
      }
    }

    "System.err should throw an exception when the listener is added" {
      shouldThrow<SystemErrWriteException> {
        System.err.println("boom")
      }
    }
  }
}