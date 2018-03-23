package io.kotlintest.extensions

import io.kotlintest.ProjectExtensions
import io.kotlintest.Spec
import io.kotlintest.runner.junit5.specs.WordSpec
import io.kotlintest.shouldBe

class SpecExtensionTest : WordSpec() {

  init {

    var a = 1
    var b = 1

    val add = object : SpecExtension {
      override fun intercept(spec: Spec, process: () -> Unit) {
        a += 2
        process()
        b += 2
      }
    }

    val mult = object : SpecExtension {
      override fun intercept(spec: Spec, process: () -> Unit) {
        a *= 2
        process()
        b *= 2
      }
    }

    ProjectExtensions.registerExtension(add)
    ProjectExtensions.registerExtension(mult)

    // use a project after all extension to test the around advice of a spec
    ProjectExtensions.registerExtension(object : ProjectExtension {
      override fun afterAll() {
       b shouldBe 4
      }
    })

    "SpecExtensions" should {
      "be activated by registration with ProjectExtensions" {
        // the sum and mult before calling test() should have fired
        a shouldBe 6
        b shouldBe 1
      }
      "only be fired once per spec class" {
        // this test, the specs should not have fired
        a shouldBe 6
        b shouldBe 1
      }
    }
  }
}