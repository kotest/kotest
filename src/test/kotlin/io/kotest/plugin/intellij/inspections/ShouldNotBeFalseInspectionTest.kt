package io.kotest.plugin.intellij.inspections

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

internal class ShouldNotBeFalseInspectionTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testInspection() {
      myFixture.configureByFile("/inspections/shouldNotBeFalse.kt")
      myFixture.enableInspections(ShouldNotBeFalseInspection::class.java)
      val highlight = myFixture.doHighlighting().find { it.description == "Replace with shouldBe true" }!!
      highlight.text shouldBe """"".isBlank() shouldNotBe false"""
      highlight.severity shouldBe HighlightSeverity.WARNING
      highlight.startOffset shouldBe 260
      highlight.endOffset shouldBe 290
   }
}
