package com.sksamuel.kt.extensions.system

import io.kotlintest.extensions.system.SpecSystemExitListener
import io.kotlintest.extensions.system.SystemExitException
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
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
