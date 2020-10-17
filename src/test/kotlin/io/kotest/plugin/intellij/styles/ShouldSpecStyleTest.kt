package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
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
      gutters.size shouldBe 10

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[0].tooltipText shouldBe "Run ShouldSpecExample"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 91

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "Run top level test"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 173

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "Run top level test with config"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 247

      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[3].tooltipText shouldBe "Run some context"
      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 333

      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[4].tooltipText shouldBe "Run some context top level test"
      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 369

      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[5].tooltipText shouldBe "Run some context top level test with config"
      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 452

      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[6].tooltipText shouldBe "Run some context 2"
      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 554

      gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[7].tooltipText shouldBe "Run some context 2 some nested context"
      (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 596

      gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[8].tooltipText shouldBe "Run some context 2 some nested context top level test"
      (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 635

      gutters[9].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[9].tooltipText shouldBe "Run some context 2 some nested context top level test with config"
      (gutters[9] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 727
   }

   fun testMethodGeneration() {
      ShouldSpecStyle.generateTest("myspec", "testName") shouldBe "should(\"testName\") { }"
   }
}
