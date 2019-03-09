package com.sksamuel.kotlintest.extensions

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

object SpecExtensionNumbers {

  var before = 0
  var after = 0

  val ext = object : SpecExtension {
    override suspend fun intercept(spec: Spec, process: suspend () -> Unit) {
      if (spec.description().name == "com.sksamuel.kotlintest.extensions.SpecExtensionTest") {
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

    Project.registerListeners(object : TestListener {
      override fun afterProject() {
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