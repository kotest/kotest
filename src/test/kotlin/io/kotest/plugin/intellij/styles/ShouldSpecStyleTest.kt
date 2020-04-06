package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class ShouldSpecStyleTest : LightCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFile("/shouldspec.kt")

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 3

      assertSoftly {

         gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
         gutters[0].tooltipText shouldBe "[Kotest] ShouldSpecExample"
         (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 91

         gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[1].tooltipText shouldBe "[Kotest] should top level test"
         (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 145

         gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[2].tooltipText shouldBe "[Kotest] should top level test with config"
         (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 237
      }
   }

   fun testMethodGeneration() {
      ShouldSpecStyle.generateTest("myspec", "testName") shouldBe "should(\"testName\") { }"
   }
}
