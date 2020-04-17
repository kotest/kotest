package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class BehaviorSpecRunMarkerTest : LightPlatformCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFile("/behaviorspec.kt")
      assertSoftly {

         val gutters = myFixture.findAllGutters()
         println(gutters.map { it.tooltipText }.joinToString("\n"))
         gutters.size shouldBe 8

         gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
         gutters[0].tooltipText shouldBe "Run BehaviorSpecExample"
         (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 175

         gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[1].tooltipText shouldBe "Run Given: a given"
         (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 251

         gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[2].tooltipText shouldBe "Run Given: a given When: a when"
         (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 276

         gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[3].tooltipText shouldBe "Run Given: a given When: a when Then: a test"
         (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 301

         gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[4].tooltipText shouldBe "Run Given: a given When: a when Then: another test"
         (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 379

         gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[5].tooltipText shouldBe "Run Given: a given When: another when"
         (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 451

         gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[6].tooltipText shouldBe "Run Given: a given When: another when Then: a test"
         (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 476

         gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
         gutters[7].tooltipText shouldBe "Run Given: a given When: another when Then: a test with config"
         (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 545
      }
   }

   fun testMethodGeneration() {
      BehaviorSpecStyle.generateTest("myspec", "testName") shouldBe "Given(\"testName\") { }"
   }
}
