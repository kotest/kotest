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
import io.kotest.extensions.system.SystemPropertyTestListener
import io.kotest.extensions.system.withSystemProperties
import io.kotest.extensions.system.withSystemProperty
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.reflect.KClass

class SystemPropertiesExtensionsTest : FreeSpec() {

   private val key = "SystemPropertiesExtensionsTestFoo"
   private val value = "SystemPropertiesExtensionsTestBar"

   private val mode: OverrideMode = mockk {
      every { override(any(), any()) } answers {
         firstArg<Map<String, String>>().plus(secondArg<Map<String, String>>()).toMutableMap()
      }
   }

   init {
      "Should set properties to specific map" - {
         executeOnAllPropertyOverloads {
            System.getProperty(key) shouldBe value
         }
      }

      "Should return original properties to their place after execution" - {
         val before = System.getProperties()

         executeOnAllPropertyOverloads {
            System.getProperties() shouldNotBe before
         }

         System.getProperties() shouldBe before

      }

      "Should return the computed value" - {
         val results = executeOnAllPropertyOverloads { "RETURNED" }

         results.forAll {
            it shouldBe "RETURNED"
         }
      }
   }

   private suspend fun <T> FreeScope.executeOnAllPropertyOverloads(block: suspend () -> T): List<T> {
      val results = mutableListOf<T>()

      "String String overload" {
         results += withSystemProperty(key, value, mode) { block() }
      }

      "Pair overload" {
         results += withSystemProperties(key to value, mode) { block() }
      }

      "Properties Overload" {
         results += withSystemProperties(Properties().apply { put(key, value) }) { block() }
      }

      "Map overload" {
         results += withSystemProperties(mapOf(key to value), mode) { block() }
      }

      return results
   }
}

@AutoScan
object Assertion : TestListener {
   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (kclass == SystemPropertyListenerTest::class) {
         System.getProperty("bee") shouldBe null
      }

   }

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == SystemPropertyListenerTest::class) {
         System.getProperty("bee") shouldBe null
      }
   }
}


class SystemPropertyListenerTest : WordSpec() {
   private val sptl = SystemPropertyTestListener("bee", "bop", mode = OverrideMode.SetOrOverride)

   override fun listeners() = listOf(sptl)

   init {
      "sys prop extension" should {
         "set sys prop" {
            System.getProperty("bee") shouldBe "bop"
         }
      }
   }
}
