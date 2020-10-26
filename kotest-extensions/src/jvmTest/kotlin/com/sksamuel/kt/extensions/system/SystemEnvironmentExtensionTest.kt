package com.sksamuel.kt.extensions.system

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.SystemEnvironmentTestListener
import io.kotest.extensions.system.withEnvironment
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KClass

class SystemEnvironmentExtensionTest : FreeSpec() {

   private val key = "SystemEnvironmentExtensionTestFoo"
   private val value = "SystemEnvironmentExtensionTestBar"

   private val mode: OverrideMode = mockk {
      every { override(any(), any()) } answers {
         firstArg<Map<String, String>>().plus(secondArg<Map<String, String>>()).toMutableMap()
      }
   }

   init {
      "Should set environment to specific map" - {
         executeOnAllEnvironmentOverloads {
            System.getenv(key) shouldBe value
         }
      }

      "Should return original environment to its place after execution" - {
         val before = System.getenv().toMap()

         executeOnAllEnvironmentOverloads {
            System.getenv() shouldNotBe before
         }
         System.getenv() shouldBe before

      }

      "Should return the computed value" - {
         val results = executeOnAllEnvironmentOverloads { "RETURNED" }

         results.forAll {
            it shouldBe "RETURNED"
         }
      }
   }

   private suspend fun <T> FreeScope.executeOnAllEnvironmentOverloads(block: suspend () -> T): List<T> {
      val results = mutableListOf<T>()

      "String String overload" {
         results += withEnvironment(key, value, mode) { block() }
      }

      "Pair overload" {
         results += withEnvironment(key to value, mode) { block() }
      }

      "Map overload" {
         results += withEnvironment(mapOf(key to value), mode) { block() }
      }

      return results
   }

}

@AutoScan
object SysEnvTestListener : TestListener {
   override val name: String
      get() = "SysEnvTestListener"

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (kclass == SystemEnvironmentTestListenerTest::class) {
         System.getenv("mop") shouldBe null
      }
   }

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == SystemEnvironmentTestListenerTest::class) {
         System.getenv("mop") shouldBe null
      }
   }
}

class SystemEnvironmentTestListenerTest : WordSpec() {

   val setl = SystemEnvironmentTestListener("mop", "dop", mode = OverrideMode.SetOrOverride)

   override fun listeners() = listOf(setl)

   init {
      "sys environment extension" should {
         "set environment variable" {
            System.getenv("mop") shouldBe "dop"
         }
      }
   }
}
