package com.sksamuel.kt.extensions.system

import io.kotest.extensions.system.NoSystemErrListener
import io.kotest.extensions.system.NoSystemOutListener
import io.kotest.extensions.system.SystemErrWriteException
import io.kotest.extensions.system.SystemOutWriteException
import io.kotest.shouldThrow
import io.kotest.specs.StringSpec

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