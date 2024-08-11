package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class WordSpecStyleTest : BasePlatformTestCase() {

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
      gutters.size shouldBe 10

      val expected = listOf(
         Gutter("Run WordSpecExample", 87, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run some should context", 137),
         Gutter("Run some should context test something", 174),
         Gutter("Run some should context test something with config", 256),
         Gutter("Run with capital When", 302),
         Gutter("Run with capital When and capital Should", 335),
         Gutter("Run with capital When test something", 373),
         Gutter("Run with capital When test something with config", 428),
         Gutter("Disabled - with capital When disabled should", 480, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - disabled when", 529, AllIcons.RunConfigurations.TestIgnored),
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
