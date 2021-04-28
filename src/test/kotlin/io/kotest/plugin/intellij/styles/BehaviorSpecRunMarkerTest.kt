package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
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
      gutters.size shouldBe 11

      val expected = listOf(
         Gutter("Run BehaviorSpecExample", 175),
         Gutter("Run a given", 251),
         Gutter("Run a given a when", 276),
         Gutter("Run a given a when a test", 301),
         Gutter("Run a given a when another test", 379),
         Gutter("Run a given another when", 451),
         Gutter("Run a given another when a test", 476),
         Gutter("Run a given another when a test with config", 545),
         Gutter("Run a given an and", 637),
         Gutter("Run a given an and a when", 664),
         Gutter("Run a given an and a when a test", 691),
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
