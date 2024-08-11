package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class ExpectSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {
      testMode = true

      myFixture.configureByFiles(
         "/expectspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      gutters.size shouldBe 7

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
      gutters[0].tooltipText shouldBe "Run ExpectSpecExample"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 87

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "Run some context"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 165

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "Run some context some test"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 193

      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[3].tooltipText shouldBe "Run some context some test with config"
      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 261

      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[4].tooltipText shouldBe "Run some context another nested context"
      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 357

      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[5].tooltipText shouldBe "Run some context another nested context some test"
      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 387

      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[6].tooltipText shouldBe "Run some context another nested context some test with config"
      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 461
   }
}
