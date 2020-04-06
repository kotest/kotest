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
         gutters.size shouldBe 12

         gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
         gutters[0].tooltipText shouldBe "[Kotest] DescribeSpecExample"
         (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 91

         gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[1].tooltipText shouldBe "[Kotest] Describe: some thing"
         (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 152

         gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[2].tooltipText shouldBe "[Kotest] Describe: some thing It: test name"
         (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 186

         gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[3].tooltipText shouldBe "[Kotest] Describe: some thing xDescribe: ignored describe"
         (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 249

         gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[4].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context"
         (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 302

         gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[5].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context It: test name"
         (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 345

         gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[6].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context It: test name 2"
         (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 434

         gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[7].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context Context: yet another context"
         (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 515

         gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[8].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context Context: yet another context It: test name"
         (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 563

         gutters[9].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[9].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context Context: yet another context xIt: ignored test"
         (gutters[9] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 644

         gutters[10].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[10].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context Context: yet another context It: test name 2"
         (gutters[10] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 746

         gutters[11].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[11].tooltipText shouldBe "[Kotest] Describe: some thing Context: with some context Context: yet another context It: ignored test with config"
         (gutters[11] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 867

      }
   }

  fun testMethodGeneration() {
    DescribeSpecStyle.generateTest("myspec", "testName") shouldBe "describe(\"testName\") { }"
  }
}
