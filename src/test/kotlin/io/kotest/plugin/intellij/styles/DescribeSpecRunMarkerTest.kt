package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
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

      myFixture.configureByFiles(
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()

      val expected = listOf(
         Gutter("Run DescribeSpecExample", 91, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run describe block", 179),
         Gutter("Run describe block it block", 205),
         Gutter("Disabled - describe block xit block", 269, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run describe block it with config", 336),
         Gutter("Disabled - describe block xit block with config", 436, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run describe block nested describe block", 542),
         Gutter("Run describe block nested describe block it block", 571),
         Gutter("Disabled - describe block nested xdescribe block", 671, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - describe block nested xdescribe block it block", 700, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block", 798, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block it block", 824, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block xit block", 888, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block it with config", 955, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block xit block with config", 1055, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block nested describe block", 1161, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block nested describe block it block", 1190, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block nested xdescribe block", 1290, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block nested xdescribe block it block", 1319, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run context block", 1413),
         Gutter("Run context block nested context block", 1456),
         Gutter("Run context block nested context block nested describe block", 1504),
         Gutter("Run context block nested context block nested describe block it block", 1536),
         Gutter("Disabled - context block nested context block nested xdescribe block", 1648, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - context block nested context block nested xdescribe block it block", 1680, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - context block nested xcontext block", 1798, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - context block nested xcontext block nested describe block", 1846, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - context block nested xcontext block nested describe block it block", 1878, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - context block nested xcontext block nested xdescribe block", 1990, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - context block nested xcontext block nested xdescribe block it block", 2022, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run context block nested describe block", 2140),
         Gutter("Run context block nested describe block it block", 2169),
         Gutter("Run describe with config", 2270),
         Gutter("Run describe with config it block", 2320),
         Gutter("Disabled - xdescribe with config", 2406, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe with config it block", 2456, AllIcons.RunConfigurations.TestIgnored),
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
