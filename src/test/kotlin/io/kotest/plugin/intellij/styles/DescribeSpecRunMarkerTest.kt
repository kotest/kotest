package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.BasePlatformTestCase
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

class DescribeSpecRunMarkerTest : BasePlatformTestCase() {

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
         Gutter("Run DescribeSpecExample", 91, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run describe block", 161),
         Gutter("Run describe block it block", 193),
         Gutter("Disabled - describe block xit block", 256, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run describe block it with config", 336),
         Gutter("Disabled - describe block xit block with config", 436, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run describe block nested describe block", 517),
         Gutter("Run describe block nested describe block it block", 559),
         Gutter("Disabled - describe block nested xdescribe block", 645, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - describe block nested xdescribe block it block",
            688,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - xdescribe block", 779, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block it block", 812, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block xit block", 875, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block it with config", 955, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block xit block with config", 1055, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe block nested describe block", 1136, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - xdescribe block nested describe block it block",
            1178,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - xdescribe block nested xdescribe block", 1264, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - xdescribe block nested xdescribe block it block",
            1307,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Run context block", 1396),
         Gutter("Run context block nested context block", 1432),
         Gutter("Run context block nested context block nested describe block", 1479),
         Gutter("Run context block nested context block nested describe block it block", 1524),
         Gutter(
            "Disabled - context block nested context block nested xdescribe block",
            1622,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested context block nested xdescribe block it block",
            1668,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - context block nested xcontext block", 1773, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - context block nested xcontext block nested describe block",
            1821,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested xcontext block nested describe block it block",
            1866,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested xcontext block nested xdescribe block",
            1964,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - context block nested xcontext block nested xdescribe block it block",
            2010,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Run context block nested describe block", 2115),
         Gutter("Run context block nested describe block it block", 2157),
         Gutter("Run describe with config", 2270),
         Gutter("Run describe with config it block", 2308),
         Gutter("Disabled - xdescribe with config", 2406, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xdescribe with config it block", 2444, AllIcons.RunConfigurations.TestIgnored),
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
