package io.kotest.plugin.intellij.annotators

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import java.nio.file.Paths

internal class DuplicatedTestNameAnnotatorTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testInspection() {
      myFixture.configureByFile("/annotators/duplicatedFunSpecTest.kt")
      val highlights = myFixture.doHighlighting()
//      highlight.text shouldBe "(a is Long) shouldBe true"
//      highlight.severity shouldBe HighlightSeverity.WARNING
//      highlight.startOffset shouldBe 190
//      highlight.endOffset shouldBe 215
      println(highlights)
   }
}
