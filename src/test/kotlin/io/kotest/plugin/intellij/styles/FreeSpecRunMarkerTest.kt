package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class FreeSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {
      testMode = true

      myFixture.configureByFiles(
         "/freespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 8

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
      gutters[0].tooltipText shouldBe "Run FreeSpecExample"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 87

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "Run some context"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 152

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "Run some context more context"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 177

      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[3].tooltipText shouldBe "Run some context more context as many as you want"
      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 211

      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[4].tooltipText shouldBe "Run some context more context as many as you want then a test"
      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 225

      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[5].tooltipText shouldBe "Run another context"
      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 324

      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[6].tooltipText shouldBe "Run another context a test with config"
      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 354

      gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[7].tooltipText shouldBe "Run a test without a context block"
      (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 399
   }

   fun testMethodGeneration() {
      FreeSpecStyle.generateTest("myspec", "testName") shouldBe "\"testName\" { }"
   }
}
