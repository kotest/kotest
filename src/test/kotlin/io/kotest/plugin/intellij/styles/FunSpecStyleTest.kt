package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class FunSpecStyleTest : LightCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFile("/funspec.kt")

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 9

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
      gutters[0].tooltipText shouldBe "[Kotest] FunSpecExampleTest"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 228

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "[Kotest] a string cannot be blank"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 268

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "[Kotest] a string should be lower case"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 379

      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[3].tooltipText shouldBe "[Kotest] some context"
      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 445

      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[4].tooltipText shouldBe "[Kotest] some context -- a string cannot be blank"
      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 476

      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[5].tooltipText shouldBe "[Kotest] some context -- a string should be lower case"
      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 593

      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[6].tooltipText shouldBe "[Kotest] some context -- another context"
      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 665

      gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[7].tooltipText shouldBe "[Kotest] some context -- another context -- a string cannot be blank"
      (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 701

      gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[8].tooltipText shouldBe "[Kotest] some context -- another context -- a string should be lower case"
      (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 824

   }

   fun testMethodGeneration() {
      FunSpecStyle.generateTest("myspec", "testName") shouldBe "test(\"testName\") { }"
   }
}
