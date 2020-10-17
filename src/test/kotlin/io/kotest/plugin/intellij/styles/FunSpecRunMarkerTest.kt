package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class FunSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      gutters.size shouldBe 9

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[0].tooltipText shouldBe "Run FunSpecExampleTest"
      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 228

      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[1].tooltipText shouldBe "Run a string cannot be blank"
      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 301

      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[2].tooltipText shouldBe "Run a string should be lower case"
      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 379

      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[3].tooltipText shouldBe "Run some context"
      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 469

      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[4].tooltipText shouldBe "Run some context a string cannot be blank"
      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 509

      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[5].tooltipText shouldBe "Run some context a string should be lower case"
      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 593

      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[6].tooltipText shouldBe "Run some context another context"
      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 692

      gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[7].tooltipText shouldBe "Run some context another context a string cannot be blank"
      (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 734

      gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
      gutters[8].tooltipText shouldBe "Run some context another context a string should be lower case"
      (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 824
   }

   fun testMethodGeneration() {
      FunSpecStyle.generateTest("myspec", "testName") shouldBe "test(\"testName\") { }"
   }
}
