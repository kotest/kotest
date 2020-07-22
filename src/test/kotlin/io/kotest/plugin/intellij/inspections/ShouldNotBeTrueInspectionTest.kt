package io.kotest.plugin.intellij.inspections

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

internal class ShouldNotBeTrueInspectionTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testInspection() {
      myFixture.configureByFile("/inspections/shouldNotBeTrue.kt")
      myFixture.enableInspections(ShouldNotBeTrueInspection::class.java)
      val highlight = myFixture.doHighlighting().find { it.description == "Replace with shouldBe false" }!!
      highlight.text shouldBe """"sdfdsf".isBlank() shouldNotBe true"""
      highlight.severity shouldBe HighlightSeverity.WARNING
      highlight.startOffset shouldBe 274
      highlight.endOffset shouldBe 309
   }
}
