package io.kotlintest.plugin.intellij.psi

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.shouldBe
import java.nio.file.Paths

class ExpectSpecStyleTest : LightCodeInsightFixtureTestCase() {

  override fun getTestDataPath(): String {
    val path = Paths.get("./src/test/resources/").toAbsolutePath()
    return path.toString()
  }

  fun testGutterIcons() {

    myFixture.configureByFile("/expectspec.kt")

    val gutters = myFixture.findAllGutters()
    gutters.size shouldBe 7

    gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run_run
    gutters[0].tooltipText shouldBe "[KotlinTest] ExpectSpecExample"
    (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 85

    gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[1].tooltipText shouldBe "[KotlinTest] some context"
    (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 148

    gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[2].tooltipText shouldBe "[KotlinTest] some context -- some test"
    (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 179

    gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[3].tooltipText shouldBe "[KotlinTest] some context -- some test 2"
    (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 236

    gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[4].tooltipText shouldBe "[KotlinTest] some context -- another nested context"
    (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 320

    gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[5].tooltipText shouldBe "[KotlinTest] some context -- another nested context -- some test"
    (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 363

    gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[6].tooltipText shouldBe "[KotlinTest] some context -- another nested context -- some test 2"
    (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 426

  }
}