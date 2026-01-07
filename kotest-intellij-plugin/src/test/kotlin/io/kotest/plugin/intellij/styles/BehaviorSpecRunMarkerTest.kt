package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class BehaviorSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {
      testMode = true

      myFixture.configureByFiles(
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 35

      val expected = listOf(
         Gutter("Run BehaviorSpecExample", 126, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run a given", 194),
         Gutter("Run a given a when", 223),
         Gutter("Run a given a when a test", 252),
         Gutter("Run a given a when another test", 295),
         Gutter("Disabled - a given a when a disabled then", 345, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled when", 406, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - a given disabled when this then should be disabled from its parent",
            442,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - a given disabled when this then should be disabled with config",
            566,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Run a given an and", 631),
         Gutter("Run a given an and a when", 662),
         Gutter("Run a given an and a when a test", 694),
         Gutter("Run a given an and an and in an and", 753),
         Gutter("Run a given an and an and in an and a test", 795),
         Gutter("Disabled - a given an and disabled when", 856, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - a given an and disabled when this then should be disabled by nesting",
            895,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - a given an and disabled when an xdisabled then",
            978,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - a given disabled and", 1057, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and a nested when", 1094, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and a nested when a test", 1133, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given", 1208, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled when", 1242, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - disabled given disabled when a disabled then",
            1278,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - disabled given disabled and", 1337, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled and a test", 1372, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given", 1430, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given a nested then", 1464, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run a context", 1507),
         Gutter("Run a context a nested given", 1534),
         Gutter("Run a context a nested given a when", 1570),
         Gutter("Run a context a nested given a when a test", 1599),
         Gutter("Disabled - a context disabled given", 1657, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a context disabled given a disabled when", 1691, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a context disabled given a disabled when a disabled test", 1729, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run All Spec Tests, including data tests", 1815),
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
      BehaviorSpecStyle.generateTest("myspec", "testName") shouldBe "given(\"testName\") { }"
   }
}
