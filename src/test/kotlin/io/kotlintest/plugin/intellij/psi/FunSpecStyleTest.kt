package io.kotlintest.plugin.intellij.psi

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.shouldBe
import java.nio.file.Paths

class FunSpecStyleTest : LightCodeInsightFixtureTestCase() {

  override fun getTestDataPath(): String {
    val path = Paths.get("./src/test/resources/").toAbsolutePath()
    return path.toString()
  }

  fun testGutterIcons() {

    myFixture.configureByFile("/funspec.kt")

    val gutters = myFixture.findAllGutters()
    gutters.size shouldBe 2

    gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[0].tooltipText shouldBe "[KotlinTest] a string cannot be blank"
    (gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 229

    gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Run
    gutters[1].tooltipText shouldBe "[KotlinTest] a string should be lower case"
    (gutters[1] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe 303

  }
}