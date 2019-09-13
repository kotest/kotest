package com.sksamuel.kt.extensions.system

import io.kotest.extensions.system.SpecSystemExitListener
import io.kotest.extensions.system.SystemExitException
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.StringSpec
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
