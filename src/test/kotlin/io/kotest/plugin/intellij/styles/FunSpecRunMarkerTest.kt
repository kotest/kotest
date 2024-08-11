package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths

class FunSpecRunMarkerTest : BasePlatformTestCase() {

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
         Gutter("Run FunSpecExampleTest", 76, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run a test", 122),
         Gutter("Run a test with config", 169),
         Gutter("Disabled - an xtest", 210, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xtest with config", 262, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run some context", 305),
         Gutter("Run some context a nested test", 335),
         Gutter("Run some context a nested test with config", 402),
         Gutter("Disabled - some context a nested xtest", 449, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context a nested xtest with config", 519, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run some context a nested context", 568),
         Gutter("Run some context a nested context a test", 605),
         Gutter(
            "Disabled - some context a nested context a nested xcontext",
            647,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a test",
            687,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a nested context",
            733,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a nested context a test",
            775,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - some context an xcontext", 853, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext a test", 884, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext an xtest", 922, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext a nested xcontext", 966, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - some context an xcontext a nested xcontext a test",
            1006,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - an xcontext", 1069, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a test", 1097, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a nested xcontext", 1131, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a nested xcontext a test", 1168, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run context with config", 1238),
         Gutter("Run context with config a test inside a context with config", 1275),
         Gutter("Disabled - xcontext with config", 1365, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - xcontext with config a test inside an xcontext with config",
            1402,
            AllIcons.RunConfigurations.TestIgnored
         ),

         // Previously the PSI text was fetched which contains the raw text entered, not parsed string, which means it would include the backslashes as well.
         // See https://github.com/kotest/kotest/issues/3078
         Gutter("""Run name containing "escaped quotes"""", 1467),
      )

//      gutters.size shouldBe expected.size

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
