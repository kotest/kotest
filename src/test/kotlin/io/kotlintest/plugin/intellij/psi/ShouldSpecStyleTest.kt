package io.kotlintest.plugin.intellij.psi

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.shouldBe
import java.nio.file.Paths

class ShouldSpecStyleTest : LightCodeInsightFixtureTestCase() {

  override fun getTestDataPath(): String {
    val path = Paths.get("./src/test/resources/").toAbsolutePath()
    return path.toString()
  }

  fun testGutterIcons() {

    myFixture.configureByFile("/shouldspec.kt")

    val gutters = myFixture.findAllGutters()
    gutters.size shouldBe 8

    gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[0].tooltipText shouldBe "[KotlinTest] should top level test"
    (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 151

    gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[1].tooltipText shouldBe "[KotlinTest] should top level test with config"
    (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 207

    gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[2].tooltipText shouldBe "[KotlinTest] a context"
    (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 291

    gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[3].tooltipText shouldBe "[KotlinTest] a context should do a test"
    (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 318

    gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[4].tooltipText shouldBe "[KotlinTest] a context should have config"
    (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 375

    gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[5].tooltipText shouldBe "[KotlinTest] another context"
    (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 454

    gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[6].tooltipText shouldBe "[KotlinTest] another context -- a nested context"
    (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 480

    gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[7].tooltipText shouldBe "[KotlinTest] another context -- a nested context should do a test"
    (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 516
  }
}