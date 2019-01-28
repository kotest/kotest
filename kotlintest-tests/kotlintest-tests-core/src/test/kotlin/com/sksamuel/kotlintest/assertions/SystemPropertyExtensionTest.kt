package com.sksamuel.kotlintest.assertions

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.SystemPropertyExtension
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class SystemPropertyExtensionTest : WordSpec() {

  override fun extensions(): List<SpecLevelExtension> = listOf(SystemPropertyExtension("wibble", "wobble"))

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