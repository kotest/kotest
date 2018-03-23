package io.kotlintest.extensions

import io.kotlintest.ProjectExtensions
import io.kotlintest.Spec
import io.kotlintest.runner.junit5.specs.WordSpec
import io.kotlintest.shouldBe
import java.util.concurrent.atomic.AtomicInteger

object SpecExtensionNumbers {

  val a = AtomicInteger(1)
  val b = AtomicInteger(1)

  val add1 = object : SpecExtension {
    override fun intercept(spec: Spec, process: () -> Unit) {
      if (spec.name() == "SpecExtensionTest") {
        SpecExtensionNumbers.a.addAndGet(2)
        process()
        SpecExtensionNumbers.b.addAndGet(2)
      } else {
        process()
      }
    }
  }

  val add2 = object : SpecExtension {
    override fun intercept(spec: Spec, process: () -> Unit) {
      if (spec.name() == "SpecExtensionTest") {
        SpecExtensionNumbers.a.addAndGet(3)
        println(SpecExtensionNumbers.a.get())
        process()
        SpecExtensionNumbers.b.addAndGet(3)
      } else {
        process()
      }
    }
  }
}

class SpecExtensionTest : WordSpec() {

  init {

    ProjectExtensions.registerExtension(SpecExtensionNumbers.add1)
    ProjectExtensions.registerExtension(SpecExtensionNumbers.add2)

    // use a project after all extension to test the around advice of a spec
    ProjectExtensions.registerExtension(object : ProjectExtension {
      override fun afterAll() {
        SpecExtensionNumbers.b.get() shouldBe 6
      }
    })

    "SpecExtensions" should {
      "be activated by registration with ProjectExtensions" {
        SpecExtensionNumbers.a.get() shouldBe 6
        SpecExtensionNumbers.b.get() shouldBe 1
      }
      "only be fired once per spec class" {
        // this test, the intercepts should not have fired
        SpecExtensionNumbers.a.get() shouldBe 6
        SpecExtensionNumbers.b.get() shouldBe 1
      }
    }
  }
}