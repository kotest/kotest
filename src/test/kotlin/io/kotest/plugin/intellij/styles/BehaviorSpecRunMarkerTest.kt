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
      gutters.size shouldBe 34

      val expected = listOf(
         Gutter("Run BehaviorSpecExample", 91, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run a given", 159),
         Gutter("Run a given a when", 188),
         Gutter("Run a given a when a test", 217),
         Gutter("Run a given a when another test", 260),
         Gutter("Disabled - a given a when a disabled then", 310, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled when", 371, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - a given disabled when this then should be disabled from its parent",
            407,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - a given disabled when this then should be disabled with config",
            531,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Run a given an and", 596),
         Gutter("Run a given an and a when", 627),
         Gutter("Run a given an and a when a test", 659),
         Gutter("Run a given an and an and in an and", 718),
         Gutter("Run a given an and an and in an and a test", 760),
         Gutter("Disabled - a given an and disabled when", 821, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - a given an and disabled when this then should be disabled by nesting",
            860,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - a given an and disabled when an xdisabled then",
            943,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - a given disabled and", 1022, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and a nested when", 1059, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and a nested when a test", 1098, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given", 1173, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled when", 1207, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - disabled given disabled when a disabled then",
            1243,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - disabled given disabled and", 1302, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled and a test", 1337, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given", 1395, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given a nested then", 1429, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run a context", 1472),
         Gutter("Run a context a nested given", 1499),
         Gutter("Run a context a nested given a when", 1535),
         Gutter("Run a context a nested given a when a test", 1564),
         Gutter("Disabled - a context disabled given", 1622, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a context disabled given a disabled when", 1656, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a context disabled given a disabled when a disabled test", 1694, AllIcons.RunConfigurations.TestIgnored),
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
