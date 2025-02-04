package io.kotest.plugin.intellij.intentions

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.idea.core.moveCaret
import java.nio.file.Paths

class ShouldThrowIntentionTest : LightJavaCodeInsightFixtureTestCase() {

   init {
      testMode = true
   }

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testIntentionForPartialLine() {
      myFixture.configureByFiles(
         "/intentions/should_throw.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )
      editor.moveCaret(649)
      editor.selectionModel.setSelection(649, 651)

      val intention = myFixture.findSingleIntention("Surround statements with shouldThrow assertion")
      intention.familyName shouldBe "Surround statements with shouldThrow assertion"

      CommandProcessor.getInstance().runUndoTransparentAction {
         runWriteAction {
            intention.invoke(project, editor, file)
         }
      }

      file.text shouldBe """package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow

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
}
"""
   }

   fun testIntentionForFullSelection() {

      myFixture.configureByFiles(
         "/intentions/should_throw.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      editor.moveCaret(631)
      editor.selectionModel.setSelection(631, 658)

      val intention = myFixture.findSingleIntention("Surround statements with shouldThrow assertion")
      intention.familyName shouldBe "Surround statements with shouldThrow assertion"

      CommandProcessor.getInstance().runUndoTransparentAction {
         runWriteAction {
            intention.invoke(project, editor, file)
         }
      }

      file.text shouldBe """package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow

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
}
"""
   }
}
