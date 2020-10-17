package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class DescribeSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      gutters.size shouldBe 15

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[0].tooltipText shouldBe "Run DescribeSpecExample"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 91

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "Run some thing"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 175

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "Run some thing test name"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 202

      gutters[3].icon shouldBe AllIcons.Nodes.TestIgnored
      gutters[3].tooltipText shouldBe "Test is disabled"
      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 280

      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[4].tooltipText shouldBe "Run some thing xignored describe"
      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 280

      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[5].tooltipText shouldBe "Run some thing with some context"
      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 333

      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[6].tooltipText shouldBe "Run some thing with some context test name"
      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 363

      gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[7].tooltipText shouldBe "Run some thing with some context test name 2"
      (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 436

      gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[8].tooltipText shouldBe "Run some thing with some context yet another context"
      (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 549

      gutters[9].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[9].tooltipText shouldBe "Run some thing with some context yet another context test name"
      (gutters[9] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 582

      gutters[10].icon shouldBe AllIcons.Nodes.TestIgnored
      gutters[10].tooltipText shouldBe "Test is disabled"
      (gutters[10] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 668

      gutters[11].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[11].tooltipText shouldBe "Run some thing with some context yet another context xignored test"
      (gutters[11] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 668

      gutters[12].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[12].tooltipText shouldBe "Run some thing with some context yet another context test name 2"
      (gutters[12] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 750

      gutters[13].icon shouldBe AllIcons.Nodes.TestIgnored
      gutters[13].tooltipText shouldBe "Test is disabled"
      (gutters[13] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 872

      gutters[14].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[14].tooltipText shouldBe "Run some thing with some context yet another context xignored test with config"
      (gutters[14] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 872
   }

   fun testMethodGeneration() {
      DescribeSpecStyle.generateTest("myspec", "testName") shouldBe "describe(\"testName\") { }"
   }
}
