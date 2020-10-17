package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class StringSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/stringspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 3

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[0].tooltipText shouldBe "Run StringSpecExample"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 91

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "Run this is a test"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 145

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "Run this test has config"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 215

   }

   fun testMethodGeneration() {
      StringSpecStyle.generateTest("myspec", "testName") shouldBe "\"testName\" { }"
   }
}
