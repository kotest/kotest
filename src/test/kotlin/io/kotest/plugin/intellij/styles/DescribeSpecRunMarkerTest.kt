package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths
import javax.swing.Icon

data class Gutter(
   val tooltip: String,
   val offset: Int = 0,
   val icon: Icon = AllIcons.RunConfigurations.TestState.Run
)

class DescribeSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {
      testMode = true

      myFixture.configureByFiles(
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()

      val expected = listOf(
         Gutter("Run DescribeSpecExample", 126, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run describe block", 196),
         Gutter("Run describe block it block", 228),
         Gutter("Disabled - describe block xit block", 291, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run describe block it with config", 371),
         Gutter("Disabled - describe block xit block with config", 471, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run describe block nested describe block", 552),
         Gutter("Run describe block nested describe block it block", 594),
         Gutter("Disabled - describe block nested xdescribe block", 680, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - describe block nested xdescribe block it block",
            723,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - xdescribe block", 814, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block it block", 847, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block xit block", 910, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block it with config", 990, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block xit block with config", 1090, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block nested describe block", 1171, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - xdescribe block nested describe block it block",
            1213,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - xdescribe block nested xdescribe block", 1299, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - xdescribe block nested xdescribe block it block",
            1342,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Run context block", 1431),
         Gutter("Run context block nested context block", 1467),
         Gutter("Run context block nested context block nested describe block", 1514),
         Gutter("Run context block nested context block nested describe block it block", 1559),
         Gutter(
            "Disabled - context block nested context block nested xdescribe block",
            1657,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested context block nested xdescribe block it block",
            1703,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - context block nested xcontext block", 1808, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - context block nested xcontext block nested describe block",
            1856,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested xcontext block nested describe block it block",
            1901,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested xcontext block nested xdescribe block",
            1999,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested xcontext block nested xdescribe block it block",
            2045,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Run context block nested describe block", 2150),
         Gutter("Run context block nested describe block it block", 2192),
         Gutter("Run describe with config", 2305),
         Gutter("Run describe with config it block", 2343),
         Gutter("Disabled - xdescribe with config", 2441, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe with config it block", 2479, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run context with config", 2573),
         Gutter("Run context with config nested describe with config", 2647),
         Gutter("Run context with config nested describe with config it block", 2688),
         Gutter("Disabled - xcontext with config", 2801, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run All Spec Tests, including data tests", 2866),
      )

      expected.size shouldBe gutters.size

      assertSoftly {
         expected.withIndex().forEach { (index, a) ->
            gutters[index].tooltipText shouldBe a.tooltip
            (gutters[index] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe a.offset
            gutters[index].icon shouldBe a.icon
         }
      }
   }

   fun testMethodGeneration() {
      DescribeSpecStyle.generateTest("myspec", "testName") shouldBe "describe(\"testName\") { }"
   }
}
