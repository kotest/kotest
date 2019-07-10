package com.sksamuel.kt.extensions.system

import io.kotlintest.*
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.extensions.system.OverrideMode
import io.kotlintest.extensions.system.SystemPropertyTestListener
import io.kotlintest.extensions.system.withSystemProperties
import io.kotlintest.extensions.system.withSystemProperty
import io.kotlintest.inspectors.forAll
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import java.util.*

class SystemPropertiesExtensionsTest : FreeSpec() {

  private val key = "SystemPropertiesExtensionsTestFoo"
  private val value = "SystemPropertiesExtensionsTestBar"

  private val mode: OverrideMode = mockk {
    every { override(any(), any()) } answers { firstArg<Map<String, String>>().plus(secondArg<Map<String,String>>()).toMutableMap() }
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

  private suspend fun <T> FreeSpecScope.executeOnAllPropertyOverloads(block: suspend () -> T): List<T> {
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

class SystemPropertyListenerTest : WordSpec() {

  override fun listeners() = listOf(SystemPropertyTestListener("wibble", "wobble"))

  override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
    System.getProperty("wibble") shouldBe null
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    System.getProperty("wibble") shouldBe null
  }

  init {
    "sys prop extension" should {
      "set sys prop" {
        System.getProperty("wibble") shouldBe "wobble"
      }
    }
  }
}