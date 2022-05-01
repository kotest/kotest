package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class ShouldSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/shouldspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 15

      val expected = listOf(
         Gutter("Run ShouldSpecExample", 91, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run top level test", 173),
         Gutter("Run top level test with config", 247),
         Gutter("Run some context", 333),
         Gutter("Run some context top level test", 369),
         Gutter("Run some context top level test with config", 452),
         Gutter("Run some context 2", 554),
         Gutter("Run some context 2 some nested context", 596),
         Gutter("Run some context 2 some nested context top level test", 635),
         Gutter("Run some context 2 some nested context top level test with config", 727),
         Gutter("Run a context with config", 852),
         Gutter("Run a context with config a should", 906),
         Gutter("Disabled - an xcontext with config", 958, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext with config a should", 1012, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdisabled should", 1056, AllIcons.RunConfigurations.TestIgnored),
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
