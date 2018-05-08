package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

object Numbers {
  val a = AtomicInteger(1)
  val b = AtomicInteger(1)
}

class TestCaseExtensionAdder(val n: Int) : TestCaseExtension {
  override fun intercept(context: TestCaseInterceptContext,
                         test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                         complete: (TestResult) -> Unit) {
    when (context.description.name) {
      "should be activated by registration with ProjectExtensions", "should use around advice", "should use extensions registered on config" -> {
        Numbers.a.addAndGet(n)
        test(context.config, {
          Numbers.b.addAndGet(n)
          complete(it)
        })
      }
      else -> test(context.config, { complete(it) })
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