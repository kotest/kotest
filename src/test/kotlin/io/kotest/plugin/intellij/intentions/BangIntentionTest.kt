package io.kotest.plugin.intellij.intentions

import com.intellij.openapi.command.CommandProcessor
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.nio.file.Paths

class BangIntentionTest : BasePlatformTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testIntention() {

      myFixture.configureByFiles(
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )
      editor.moveCaret(265)

      val intention = myFixture.findSingleIntention("Add/Remove bang to test name")
      intention.familyName shouldBe "Add/Remove bang to test name"
      CommandProcessor.getInstance().runUndoTransparentAction {
         runWriteAction {
            intention.invoke(project, editor, file)
         }
      }
      file.findElementAt(265)?.text shouldBe "!another test"

      CommandProcessor.getInstance().runUndoTransparentAction {
         runWriteAction {
            intention.invoke(project, editor, file)
         }
      }
      file.findElementAt(265)?.text shouldBe "another test"
   }

   fun testIntentionShouldOnlyBeAvailableOnStrings() {

      myFixture.configureByFile("/behaviorspec.kt")
      editor.moveCaret(367)
      myFixture.filterAvailableIntentions("Add/Remove bang to test name").shouldBeEmpty()
   }
}
