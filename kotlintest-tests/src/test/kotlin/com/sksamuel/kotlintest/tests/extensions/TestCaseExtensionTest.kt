package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.Project
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

object Numbers {

  val a = AtomicInteger(1)
  val b = AtomicInteger(1)

  fun add(n: Int) = object : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      if (context.description.name.contains("ZZQQ")) {
        a.addAndGet(n)
        test(context.config, {
          b.addAndGet(n)
          complete(it)
        })
      } else {
        test(context.config, { complete(it) })
      }
    }
  }

  val add1 = add(1)
  val add2 = add(2)

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
      "use extensions registered on config ZZQQ".config(extensions = listOf(Numbers.add1)) {
        Numbers.a.get() shouldBe 11
      }
    }
  }
}