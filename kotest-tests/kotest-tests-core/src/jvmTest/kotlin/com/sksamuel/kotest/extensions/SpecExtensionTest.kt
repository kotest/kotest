package com.sksamuel.kotest.extensions

import io.kotest.Project
import io.kotest.Spec
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.TestListener
import io.kotest.shouldBe
import io.kotest.specs.WordSpec

object SpecExtensionNumbers {

  var before = 0
  var after = 0

  val ext = object : SpecExtension {
    override suspend fun intercept(spec: Spec, process: suspend () -> Unit) {
      if (spec.description().name == "com.sksamuel.kotest.extensions.SpecExtensionTest") {
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