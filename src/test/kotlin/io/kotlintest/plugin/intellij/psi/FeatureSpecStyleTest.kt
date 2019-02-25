package io.kotlintest.plugin.intellij.psi

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.shouldBe
import java.nio.file.Paths

class FeatureSpecStyleTest : LightCodeInsightFixtureTestCase() {

  override fun getTestDataPath(): String {
    val path = Paths.get("./src/test/resources/").toAbsolutePath()
    return path.toString()
  }

  fun testGutterIcons() {

    myFixture.configureByFile("/featurespec.kt")

    val gutters = myFixture.findAllGutters()
    gutters.size shouldBe 9

    gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[0].tooltipText shouldBe "[KotlinTest] Feature: no scenario"
    (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 209

    gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[1].tooltipText shouldBe "[KotlinTest] Feature: some feature"
    (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 273

    gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[2].tooltipText shouldBe "[KotlinTest] Feature: some feature Scenario: some scenario"
    (gutters[2] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 306

    gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[3].tooltipText shouldBe "[KotlinTest] Feature: another feature"
    (gutters[3] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 382

    gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[4].tooltipText shouldBe "[KotlinTest] Feature: another feature Scenario: test with config"
    (gutters[4] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 418

    gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[5].tooltipText shouldBe "[KotlinTest] Feature: this feature"
    (gutters[5] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 534

    gutters[6].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[6].tooltipText shouldBe "[KotlinTest] Feature: this feature And: has nested feature contexts"
    (gutters[6] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 562

    gutters[7].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[7].tooltipText shouldBe "[KotlinTest] Feature: this feature And: has nested feature contexts Scenario: test without config"
    (gutters[7] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 612

    gutters[8].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[8].tooltipText shouldBe "[KotlinTest] Feature: this feature And: has nested feature contexts Scenario: test with config"
    (gutters[8] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 696

  }
}