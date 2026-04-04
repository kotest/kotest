package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class FunSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      testMode = true

      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()

      val expected = listOf(
         Gutter("Run FunSpecExampleTest", 111, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run a test", 157),
         Gutter("Run a test with config", 204),
         Gutter("Disabled - an xtest", 245, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xtest with config", 297, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run some context", 340),
         Gutter("Run some context a nested test", 370),
         Gutter("Run some context a nested test with config", 437),
         Gutter("Disabled - some context a nested xtest", 484, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context a nested xtest with config", 554, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run some context a nested context", 603),
         Gutter("Run some context a nested context a test", 640),
         Gutter(
            "Disabled - some context a nested context a nested xcontext",
            682,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a test",
            722,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a nested context",
            768,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a nested context a test",
            810,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - some context an xcontext", 888, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext a test", 919, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext an xtest", 957, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext a nested xcontext", 1001, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - some context an xcontext a nested xcontext a test",
            1041,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - an xcontext", 1104, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a test", 1132, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a nested xcontext", 1166, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a nested xcontext a test", 1203, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run context with config", 1273),
         Gutter("Run context with config a test inside a context with config", 1310),
         Gutter("Disabled - xcontext with config", 1400, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - xcontext with config a test inside an xcontext with config",
            1437,
            AllIcons.RunConfigurations.TestIgnored
         ),

         // Previously the PSI text was fetched which contains the raw text entered, not parsed string, which means it would include the backslashes as well.
         // See https://github.com/kotest/kotest/issues/3078
         Gutter("""Run name containing "escaped quotes"""", 1502),
         Gutter("Run All Spec Tests, including data tests", 1575),
      )

      // not sure why this works in gradle runner but not in IDE
      // going to leave it here and if it gives you trouble comment it out while you test stuff locally
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
      FunSpecStyle.generateTest("myspec", "testName") shouldBe "test(\"testName\") { }"
   }
}
