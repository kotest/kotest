package com.sksamuel.kt.extensions.system

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.extensions.system.SpecSystemExitListener
import io.kotest.extensions.system.SystemExitException
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.system.exitProcess

class SystemExitTest : StringSpec() {

  override fun listeners() = listOf(SpecSystemExitListener)

  init {

    "System.exit should throw an exception when the listener is added" {
      shouldThrow<SystemExitException> {
        exitProcess(123)
      }.exitCode shouldBe 123
    }
  }
}
