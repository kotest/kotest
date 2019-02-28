package io.kotlintest.plugin.intellij.intentions

import com.intellij.openapi.command.CommandProcessor
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.shouldBe
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.nio.file.Paths

class ShouldThrowIntentionTest : LightCodeInsightFixtureTestCase() {

  override fun getTestDataPath(): String {
    val path = Paths.get("./src/test/resources/").toAbsolutePath()
    return path.toString()
  }

  fun testIntentionForPartialLine() {

    myFixture.configureByFile("/behaviorspec.kt")
    editor.moveCaret(588)
    editor.selectionModel.setSelection(588, 592)

    val intention = myFixture.findSingleIntention("Surround statements with shouldThrow assertion")
    intention.familyName shouldBe "Surround statements with shouldThrow assertion"

    CommandProcessor.getInstance().runUndoTransparentAction {
      runWriteAction {
        intention.invoke(project, editor, file)
      }
    }

    file.text shouldBe """package com.sksamuel.kotlintest.specs.behavior

import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.kotlintest.shouldThrow

class BehaviorSpecExample : BehaviorSpec() {

  init {
    given("a given") {
      `when`("a when") {
        then("a test") {
          "sam".shouldStartWith("s")
        }
        then("another test") {
          // test here
        }
      }
      `when`("another when") {
        then("a test") {
          // test here
        }
        then("a test with config").config(invocations = 3) {
          shouldThrow<Exception> {
            1 + 1 shouldBe 2
          }
        }
      }
    }
  }
}"""
  }

  fun testIntentionForFullLine() {

    myFixture.configureByFile("/behaviorspec.kt")
    editor.moveCaret(588)
    editor.selectionModel.setSelection(569, 595)

    val intention = myFixture.findSingleIntention("Surround statements with shouldThrow assertion")
    intention.familyName shouldBe "Surround statements with shouldThrow assertion"

    CommandProcessor.getInstance().runUndoTransparentAction {
      runWriteAction {
        intention.invoke(project, editor, file)
      }
    }

    file.text shouldBe """package com.sksamuel.kotlintest.specs.behavior

import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.kotlintest.shouldThrow

class BehaviorSpecExample : BehaviorSpec() {

  init {
    given("a given") {
      `when`("a when") {
        then("a test") {
          "sam".shouldStartWith("s")
        }
        then("another test") {
          // test here
        }
      }
      `when`("another when") {
        then("a test") {
          // test here
        }
        then("a test with config").config(invocations = 3) {
          shouldThrow<Exception> {
            1 + 1 shouldBe 2
          }
        }
      }
    }
  }
}"""
  }
}