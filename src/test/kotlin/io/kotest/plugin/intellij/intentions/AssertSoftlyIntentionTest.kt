package io.kotest.plugin.intellij.intentions

import com.intellij.openapi.command.CommandProcessor
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.nio.file.Paths

class AssertSoftlyIntentionTest : LightCodeInsightFixtureTestCase() {

  override fun getTestDataPath(): String {
    val path = Paths.get("./src/test/resources/").toAbsolutePath()
    return path.toString()
  }

  fun testIntentionForPartialLine() {

    myFixture.configureByFile("/funspec.kt")
    editor.moveCaret(885)
    editor.selectionModel.setSelection(869, 912)

    val intention = myFixture.findSingleIntention("Surround statements with soft assert")
    intention.familyName shouldBe "Surround statements with soft assert"

    CommandProcessor.getInstance().runUndoTransparentAction {
      runWriteAction {
        intention.invoke(project, editor, file)
      }
    }

    file.text shouldBe """package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldBeLowerCase
import io.kotest.matchers.string.shouldBeUpperCase
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.assertions.assert.assertSoftly

class FunSpecExampleTest : FunSpec({

  test("a string cannot be blank") {
    "wibble".shouldNotBeBlank()
  }

  test("a string should be lower case").config(enabled = true) {
    "wibble".shouldBeLowerCase()
  }

  context("some context") {

    test("a string cannot be blank") {
      "wibble".shouldNotBeBlank()
    }

    test("a string should be lower case").config(enabled = true) {
      "wibble".shouldBeLowerCase()
    }

    context("another context") {

      test("a string cannot be blank") {
        "wibble".shouldNotBeBlank()
      }

      test("a string should be lower case").config(enabled = true) {
        assertSoftly {
          "wibble".shouldBeLowerCase()
          "WOBBLE".shouldBeUpperCase()
        }
      }
    }
  }

})"""
  }

  fun testIntentionForFullLine() {

    myFixture.configureByFile("/funspec.kt")
    editor.moveCaret(511)
    editor.selectionModel.setSelection(511, 544)

    val intention = myFixture.findSingleIntention("Surround statements with soft assert")
    intention.familyName shouldBe "Surround statements with soft assert"

    CommandProcessor.getInstance().runUndoTransparentAction {
      runWriteAction {
        intention.invoke(project, editor, file)
      }
    }

    file.text shouldBe """package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldBeLowerCase
import io.kotest.matchers.string.shouldBeUpperCase
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.assertions.assert.assertSoftly

class FunSpecExampleTest : FunSpec({

  test("a string cannot be blank") {
    "wibble".shouldNotBeBlank()
  }

  test("a string should be lower case").config(enabled = true) {
    "wibble".shouldBeLowerCase()
  }

  context("some context") {

    test("a string cannot be blank") {
      assertSoftly {
        "wibble".shouldNotBeBlank()
      }
    }

    test("a string should be lower case").config(enabled = true) {
      "wibble".shouldBeLowerCase()
    }

    context("another context") {

      test("a string cannot be blank") {
        "wibble".shouldNotBeBlank()
      }

      test("a string should be lower case").config(enabled = true) {
        "wibble".shouldBeLowerCase()
        "WOBBLE".shouldBeUpperCase()
      }
    }
  }

})"""
  }
}