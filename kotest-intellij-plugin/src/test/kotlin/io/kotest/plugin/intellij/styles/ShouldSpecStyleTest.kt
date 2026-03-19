package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class ShouldSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {
      testMode = true

      myFixture.configureByFiles(
         "/shouldspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 16

      val expected = listOf(
         Gutter("Run ShouldSpecExample", 126, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run top level test", 190),
         Gutter("Run top level test with config", 282),
         Gutter("Run some context", 352),
         Gutter("Run some context top level test", 386),
         Gutter("Run some context top level test with config", 487),
         Gutter("Run some context 2", 571),
         Gutter("Run some context 2 some nested context", 608),
         Gutter("Run some context 2 some nested context top level test", 652),
         Gutter("Run some context 2 some nested context top level test with config", 762),
         Gutter("Run a context with config", 887),
         Gutter("Run a context with config a should", 929),
         Gutter("Disabled - an xcontext with config", 993, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext with config a should", 1035, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdisabled should", 1072, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run All Spec Tests, including data tests", 1133),
      )

      gutters.size shouldBe expected.size

      assertSoftly {
         expected.withIndex().forEach { (index, a) ->
            gutters[index].tooltipText shouldBe a.tooltip
            (gutters[index] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe a.offset
            gutters[index].icon shouldBe a.icon
         }
      }
   }

   fun testMethodGeneration() {
      ShouldSpecStyle.generateTest("myspec", "testName") shouldBe "should(\"testName\") { }"
   }
}
