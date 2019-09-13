package com.sksamuel.kotest.extensions

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

object Numbers {
  val a = AtomicInteger(1)
  val b = AtomicInteger(1)
}

class TestCaseExtensionAdder(private val n: Int) : TestCaseExtension {
  override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit, complete: suspend (TestResult) -> Unit) {
    when (testCase.description.name) {
      "be activated by registration with ProjectExtensions", "use around advice", "use extensions registered on config" -> {
        Numbers.a.addAndGet(n)
        execute(testCase) {
          Numbers.b.addAndGet(n)
          complete(it)
        }
      }
      else -> execute(testCase) { complete(it) }
    }
  }
}

// this tests that we can use around advice with intercept
class TestCaseExtensionTest : WordSpec() {

  override fun extensions(): List<SpecLevelExtension> = listOf(TestCaseExtensionAdder(1), TestCaseExtensionAdder(2))

  init {

    "TestCaseExtensions" should {
      "be activated by registration with ProjectExtensions" {
        // the before interceptor should have incremented a but not b
        Numbers.a.get() shouldBe 4
        Numbers.b.get() shouldBe 1
      }
      "use around advice" {
        // in this second test, both the after from the previous test, and the before of this test should have fired
        Numbers.a.get() shouldBe 7
        Numbers.b.get() shouldBe 4
      }
      "use extensions registered on config".config(extensions = listOf(TestCaseExtensionAdder(3))) {
        Numbers.a.get() shouldBe 13
      }
    }
  }
}