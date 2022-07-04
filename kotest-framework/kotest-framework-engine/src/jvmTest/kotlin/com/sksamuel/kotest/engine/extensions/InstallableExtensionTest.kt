package com.sksamuel.kotest.engine.extensions

import io.kotest.core.extensions.InstallableExtension
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

class InstallableExtensionTest : FunSpec() {

   private val e = MyInstallable()

   init {
      test("InstallableExtensions should invoke configuration block") {
         val control = install(e) {
            a = "bar"
         }
         control.a shouldBe "bar"
      }

      test("InstallableExtensions should be installed as regular extensions") {
         e.before.get() shouldBe true
      }
   }
}

data class Conf(var a: String)

class MyInstallable : InstallableExtension<Conf, Conf>, BeforeTestListener {

   val before = AtomicBoolean(false)

   override suspend fun beforeTest(testCase: TestCase) {
      before.set(true)
   }

   override suspend fun mount(configure: suspend Conf.() -> Unit): Conf {
      val config = Conf("foo")
      configure(config)
      return config
   }
}
