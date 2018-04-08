package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

object Numbers {

  val a = AtomicInteger(1)
  val b = AtomicInteger(1)

  val add1 = object : TestCaseExtension {
    override fun intercept(description: Description,
                           spec: Spec,
                           config: TestCaseConfig,
                           test: (TestCaseConfig) -> TestResult): TestResult {
      return if (description.name.contains("ZZQQ")) {
        a.addAndGet(1)
        val result = test(config)
        b.addAndGet(1)
        result
      } else {
        test(config)
      }
    }
  }

  val add2 = object : TestCaseExtension {
    override fun intercept(description: Description,
                           spec: Spec,
                           config: TestCaseConfig,
                           test: (TestCaseConfig) -> TestResult): TestResult {
      return if (description.name.contains("ZZQQ")) {
        a.addAndGet(2)
        val result = test(config)
        b.addAndGet(2)
        result
      } else {
        test(config)
      }
    }
  }

  init {
    Project.registerExtension(add1)
    Project.registerExtension(add2)
  }
}

class TestCaseExtensionTest : WordSpec() {

  init {

    "TestCaseExtensions" should {
      "be activated by registration with ProjectExtensions ZZQQ" {
        // the sum and mult before calling test() should have fired
        Numbers.a.get() shouldBe 4
        Numbers.b.get() shouldBe 1
      }
      "use around advice ZZQQ" {
        // in this second test, both the after from the previous test, and the before of this test should have fired
        Numbers.a.get() shouldBe 7
        Numbers.b.get() shouldBe 4
      }
      "use extensions registered on config ZZQQ" {
        Numbers.a.get() shouldBe 11
      }.config(extensions = listOf(Numbers.add1))
    }
  }
}