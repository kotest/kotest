package com.sksamuel.kt.extensions.system

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.system.SystemEnvironmentTestListener
import io.kotlintest.extensions.system.withEnvironment
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFreeSpec
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.ShouldSpec

class SystemEnvironmentExtensionFunctionTest : FreeSpec() {
  
  init {
    "The system environment configured with a custom value" - {
      "Should contain the custom variable" - {
        val allResults = executeOnAllSystemEnvironmentOverloads("foo", "bar") {
          System.getenv("foo") shouldBe "bar"
          "RETURNED"
        }
        
        allResults.forAll { it shouldBe "RETURNED" }
      }
    }
    
    "The system environment already with a specified value" - {
      "Should become null when I set it to null" - {
        System.getenv("foo") shouldBe null  // Enforcing pre conditions
        
        withEnvironment("foo", "booz") {
          val allResults = executeOnAllSystemEnvironmentOverloads("foo", null) {
            System.getenv("foo") shouldBe null
            "RETURNED"
          }
  
          allResults.forAll { it shouldBe "RETURNED" }
  
        }
      }
    }
  }
  
  override fun afterSpec(spec: Spec) {
    verifyFooIsUnset()
  }
  
}

private suspend fun AbstractFreeSpec.FreeSpecScope.executeOnAllSystemEnvironmentOverloads(key: String, value: String?, block: suspend () -> String): List<String> {
  val results = mutableListOf<String>()
  
  "String String overload" {
    results += withEnvironment(key, value) {
      block()
    }
  }
  
  "Pair overload" {
    results += withEnvironment(key to value) { block() }
  }
  
  "Map overload" {
    results += withEnvironment(mapOf(key to value)) { block() }
  }
  
  return results
}

class SystemEnvironmentTestListenerTest : ShouldSpec() {
  
  override fun listeners() = listOf(SystemEnvironmentTestListener("foo", "bar"))
  
  init {
    should("Get extra extension from environment") {
      verifyFooIsBar()
    }
  }
  
  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    // The environment must be reset afterwards
    verifyFooIsUnset()
  }
  
}

private fun verifyFooIsBar() {
  System.getenv("foo") shouldBe "bar"
}

private fun verifyFooIsUnset() {
  System.getenv("foo") shouldBe null
}