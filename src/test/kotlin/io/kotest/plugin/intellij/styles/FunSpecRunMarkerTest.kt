package io.kotest.plugin.intellij.styles

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class FunSpecRunMarkerTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val gutters = myFixture.findAllGutters()

      val expected = listOf(
         Gutter("Run FunSpecExampleTest", 76, AllIcons.RunConfigurations.TestState.Run_run),
         Gutter("Run a test", 132),
         Gutter("Run a test with config", 169),
         Gutter("Disabled - an xtest", 222, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xtest with config", 262, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run some context", 321),
         Gutter("Run some context a nested test", 352),
         Gutter("Run some context a nested test with config", 402),
         Gutter("Disabled - some context a nested xtest", 467, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context a nested xtest with config", 519, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run some context a nested context", 588),
         Gutter("Run some context a nested context a test", 615),
         Gutter(
            "Disabled - some context a nested context a nested xcontext",
            668,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a test",
            697,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a nested context",
            753,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter(
            "Disabled - some context a nested context a nested xcontext a nested context a test",
            785,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - some context an xcontext", 868, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext a test", 894, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext an xtest", 934, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - some context an xcontext a nested xcontext", 987, AllIcons.RunConfigurations.TestIgnored),
         Gutter(
            "Disabled - some context an xcontext a nested xcontext a test",
            1016,
            AllIcons.RunConfigurations.TestIgnored
         ),
         Gutter("Disabled - an xcontext", 1084, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a test", 1107, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a nested xcontext", 1152, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - an xcontext a nested xcontext a test", 1178, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Run context with config", 1238),
         Gutter("Run context with config a test inside a context with config", 1314),
         Gutter("Disabled - xcontext with config", 1365, AllIcons.RunConfigurations.TestIgnored),
         Gutter("Disabled - xcontext with config a test inside an xcontext with config", 1443, AllIcons.RunConfigurations.TestIgnored),
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
      FunSpecStyle.generateTest("myspec", "testName") shouldBe "test(\"testName\") { }"
   }
}
