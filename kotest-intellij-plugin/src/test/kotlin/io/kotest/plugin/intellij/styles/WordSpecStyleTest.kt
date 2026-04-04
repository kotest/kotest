package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class WordSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {
      testMode = true

      myFixture.configureByFiles(
         "/wordspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()
      gutters.size shouldBe 11

      val expected = listOf(
         Gutter("Run WordSpecExample", 122, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run some should context", 172),
         Gutter("Run some should context test something", 209),
         Gutter("Run some should context test something with config", 291),
         Gutter("Run with capital When", 337),
         Gutter("Run with capital When and capital Should", 370),
         Gutter("Run with capital When test something", 408),
         Gutter("Run with capital When test something with config", 463),
         Gutter("Disabled - with capital When disabled should", 515, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled when", 564, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run All Spec Tests, including data tests", 625),
      )

      gutters.size shouldBe expected.size

      assertSoftly {
         expected.withIndex().forEach { (index, a) ->
            gutters[index].tooltipText shouldBe a.tooltip
            (gutters[index] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset shouldBe a.offset
            gutters[index].icon shouldBe a.icon
         }
      }
   }

   fun testMethodGeneration() {
      WordSpecStyle.generateTest("myspec", "testName") shouldBe "\"testName\" should { }"
   }
}
