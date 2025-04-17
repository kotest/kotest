package com.sksamuel.kt.extensions.system

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Isolate
import io.kotest.extensions.system.NoSystemErrListener
import io.kotest.extensions.system.NoSystemOutListener
import io.kotest.extensions.system.SystemErrWriteException
import io.kotest.extensions.system.SystemOutWriteException
import io.kotest.core.spec.style.StringSpec

@Isolate
class NoSystemOutOrErrTest : StringSpec() {

  override val extensions = listOf(NoSystemOutListener, NoSystemErrListener)

  init {

    "System.out should throw an exception when the listener is added" {
      shouldThrow<SystemOutWriteException> {
        println("boom")
      }
    }

    "System.err should throw an exception when the listener is added" {
      shouldThrow<SystemErrWriteException> {
        System.err.println("boom")
      }
    }
  }
}
