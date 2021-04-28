//package io.kotest.plugin.intellij.styles
//
//import com.intellij.codeInsight.daemon.LineMarkerInfo
//import com.intellij.icons.AllIcons
//import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
//import io.kotest.matchers.shouldBe
//import java.nio.file.Paths
//
//class WordSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {
//
//   override fun getTestDataPath(): String {
//      val path = Paths.get("./src/test/resources/").toAbsolutePath()
//      return path.toString()
//   }
//
//   fun testGutterIcons() {
//
//      myFixture.configureByFiles(
//         "/wordspec.kt",
//         "/io/kotest/core/spec/style/specs.kt"
//      )
//
//      val gutters = myFixture.findAllGutters()
//      gutters.size shouldBe 8
//
//      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[0].tooltipText shouldBe "Run WordSpecExample"
//      (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 87
//
//      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[1].tooltipText shouldBe "Run some should context should"
//      (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 159
//
//      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[2].tooltipText shouldBe "Run some should context should test something"
//      (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 174
//
//      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[3].tooltipText shouldBe "Run some should context should test something with config"
//      (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 256
//
//      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[4].tooltipText shouldBe "Run with capital When"
//      (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 322
//
//      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[5].tooltipText shouldBe "Run with capital When when and capital Should"
//      (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 356
//
//      gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[6].tooltipText shouldBe "Run with capital When when and capital Should should test something"
//      (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 373
//
//      gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
//      gutters[7].tooltipText shouldBe "Run with capital When when and capital Should should test something with config"
//      (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 428
//   }
//
//   fun testMethodGeneration() {
//      WordSpecStyle.generateTest("myspec", "testName") shouldBe "\"testName\" should { }"
//   }
//}
