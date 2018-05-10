package com.sksamuel.kotlintest.extensions

import io.kotlintest.Project
import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

object SpecExtensionNumbers {

  var before = 0
  var after = 0

  val ext = object : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      if (context.description.name == "SpecExtensionTest") {
        before++
        process()
        println("AFTER SPEK")
        after++
      } else {
        process()
      }
    }
  }
}

object SpecSetup {
  fun setup() {
    Project.registerExtension(SpecExtensionNumbers.ext)
  }
}

class SpecExtensionTest : WordSpec() {

  init {

    SpecSetup.setup()

    // use a project after all extension to test the around advice of a spec
    Project.registerExtension(object : ProjectExtension {
      override fun afterAll() {
        SpecExtensionNumbers.after shouldBe 1
      }
    })

    "SpecExtensions" should {
      "be activated by registration with ProjectExtensions" {
        SpecExtensionNumbers.before shouldBe 1
        SpecExtensionNumbers.after shouldBe 0
      }
      "only be fired once per spec class" {
        // the intercepts should not have fired again
        SpecExtensionNumbers.before shouldBe 1
        SpecExtensionNumbers.after shouldBe 0
      }
    }
  }
}