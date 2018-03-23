package io.kotlintest.extensions

import io.kotlintest.ProjectExtensions
import io.kotlintest.TestCase
import io.kotlintest.runner.junit5.specs.WordSpec
import io.kotlintest.shouldBe

class TestCaseExtensionTest : WordSpec() {

  init {

    var a = 1
    var b = 1

    val add = object : TestCaseExtension {
      override fun intercept(testCase: TestCase, test: () -> Unit) {
        a += 2
        test()
        b += 2
      }
    }

    val mult = object : TestCaseExtension {
      override fun intercept(testCase: TestCase, test: () -> Unit) {
        a *= 2
        test()
        b *= 2
      }
    }

    ProjectExtensions.registerExtension(add)
    ProjectExtensions.registerExtension(mult)

    "TestCaseExtensions" should {
      "be activated by registration with ProjectExtensions" {
        // the sum and mult before calling test() should have fired
        a shouldBe 6
        b shouldBe 1
      }
      "use around advice" {
        // in this second test, both the after from the previous test, and the before of this test should have fired
        a shouldBe 16
        b shouldBe 4
      }
      "use extensions registered on config" {
        a shouldBe 40
      }.config(extensions = listOf(add))
    }
  }
}