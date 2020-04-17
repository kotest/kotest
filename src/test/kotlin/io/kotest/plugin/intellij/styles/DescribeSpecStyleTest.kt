package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class DescribeSpecStyleTest : LightCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFile("/describespec.kt")

      assertSoftly {

         val gutters = myFixture.findAllGutters()
         gutters.size shouldBe 15

         gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
         gutters[0].tooltipText shouldBe "Run DescribeSpecExample"
         (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 91

         gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[1].tooltipText shouldBe "Run Describe: some thing"
         (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 175

         gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[2].tooltipText shouldBe "Run Describe: some thing It: test name"
         (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 202

         gutters[3].icon shouldBe AllIcons.Nodes.TestIgnored
         gutters[3].tooltipText shouldBe "Test is disabled"
         (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 279

         gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[4].tooltipText shouldBe "Run Describe: some thing xDescribe: ignored describe"
         (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 279

         gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[5].tooltipText shouldBe "Run Describe: some thing Describe: with some context"
         (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 332

         gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[6].tooltipText shouldBe "Run Describe: some thing Describe: with some context It: test name"
         (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 362

         gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[7].tooltipText shouldBe "Run Describe: some thing Describe: with some context It: test name 2"
         (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 435

         gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[8].tooltipText shouldBe "Run Describe: some thing Describe: with some context Describe: yet another context"
         (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 548

         gutters[9].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[9].tooltipText shouldBe "Run Describe: some thing Describe: with some context Describe: yet another context It: test name"
         (gutters[9] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 581

         gutters[10].icon shouldBe AllIcons.Nodes.TestIgnored
         gutters[10].tooltipText shouldBe "Test is disabled"
         (gutters[10] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 666

         gutters[11].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[11].tooltipText shouldBe "Run Describe: some thing Describe: with some context Describe: yet another context xIt: ignored test"
         (gutters[11] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 666

         gutters[12].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[12].tooltipText shouldBe "Run Describe: some thing Describe: with some context Describe: yet another context It: test name 2"
         (gutters[12] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 748

         gutters[13].icon shouldBe AllIcons.Nodes.TestIgnored
         gutters[13].tooltipText shouldBe "Test is disabled"
         (gutters[13] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 869

         gutters[14].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[14].tooltipText shouldBe "Run Describe: some thing Describe: with some context Describe: yet another context xIt: ignored test with config"
         (gutters[14] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 869

      }
   }

   fun testMethodGeneration() {
      DescribeSpecStyle.generateTest("myspec", "testName") shouldBe "describe(\"testName\") { }"
   }
}
