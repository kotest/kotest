package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.Project
import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

object SpecExtensionNumbers {

  val a = AtomicInteger(1)
  val b = AtomicInteger(1)

  val add1 = object : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      if (context.description.name == "SpecExtensionTest") {
        a.addAndGet(2)
        process()
        b.addAndGet(2)
      } else {
        process()
      }
    }
  }

  val add2 = object : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      if (context.description.name == "SpecExtensionTest") {
        a.addAndGet(3)
        process()
        b.addAndGet(3)
      } else {
        process()
      }
    }
  }
}

object SpecSetup {
  init {
    Project.registerExtension(SpecExtensionNumbers.add1)
    Project.registerExtension(SpecExtensionNumbers.add2)
  }
}

class SpecExtensionTest : WordSpec() {


  init {

    SpecSetup.toString()

    // use a project after all extension to test the around advice of a spec
    Project.registerExtension(object : ProjectExtension {
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