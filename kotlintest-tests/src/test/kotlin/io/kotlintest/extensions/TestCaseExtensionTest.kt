package io.kotlintest.extensions

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.extensions.Numbers.add1
import io.kotlintest.specs.WordSpec
import io.kotlintest.shouldBe
import java.util.concurrent.atomic.AtomicInteger

object Numbers {

  val a = AtomicInteger(1)
  val b = AtomicInteger(1)

  val add1 = object : TestCaseExtension {
    override fun intercept(testCase: TestCase, test: () -> Unit) {
      if (testCase.displayName.contains("ZZQQ")) {
        Numbers.a.addAndGet(2)
        test()
        Numbers.b.addAndGet(2)
      }
    }
  }

  val add2 = object : TestCaseExtension {
    override fun intercept(testCase: TestCase, test: () -> Unit) {
      if (testCase.displayName.contains("ZZQQ")) {
        Numbers.a.addAndGet(3)
        test()
        Numbers.b.addAndGet(3)
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
        Numbers.a.get() shouldBe 6
        Numbers.b.get() shouldBe 1
      }
      "use around advice ZZQQ" {
        // in this second test, both the after from the previous test, and the before of this test should have fired
        Numbers.a.get() shouldBe 11
        Numbers.b.get() shouldBe 6
      }
      "use extensions registered on config ZZQQ" {
        Numbers.a.get() shouldBe 18
      }.config(extensions = listOf(add1))
    }
  }
}