package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class BehaviorSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      println(gutters.map { it.tooltipText }.joinToString("\n"))
      gutters.size shouldBe 27

      val expected = listOf(
         Gutter("Run BehaviorSpecExample", 91, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run a given", 170),
         Gutter("Run a given a when", 198),
         Gutter("Run a given a when a test", 227),
         Gutter("Run a given a when another test", 276),
         Gutter("Disabled - a given a when a disabled then", 329, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled when", 388, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled when this then should be disabled from its parent", 455, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled when this then should be disabled with config", 531, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run a given an and", 606),
         Gutter("Run a given an and a when", 637),
         Gutter("Run a given an and a when a test", 669),
         Gutter("Run a given an and an and in an and", 738),
         Gutter("Run a given an and an and in an and a test", 770),
         Gutter("Disabled - a given an and disabled when", 838, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given an and disabled when this then should be disabled by nesting", 903, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given an and disabled when an xdisabled then", 964, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and", 1038, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and a nested when", 1076, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - a given disabled and a nested when a test", 1108, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given", 1191, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled when", 1224, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled when a disabled then", 1262, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled and", 1318, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given disabled and a test", 1347, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given", 1413, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled given a nested then", 1446, AllIcons.RunConfigurations.TestIgnored),
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
