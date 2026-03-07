package io.kotest.plugin.intellij.styles

import com.intellij.execution.TestStateStorage
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import io.kotest.plugin.intellij.testModeKotestVersion610AndAbove
import java.nio.file.Paths
import java.util.Date

class FeatureSpecTestStatusIconTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   override fun setUp() {
      super.setUp()
      testMode = true
      testModeKotestVersion610AndAbove = true
   }

   override fun tearDown() {
      try {
         val storage = TestStateStorage.getInstance(project)
         storage.keys.forEach { storage.removeState(it) }
      } finally {
         super.tearDown()
      }
   }

   fun testIconShowsFailedForFailedTest() {
      myFixture.configureByFiles(
         "/featurespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.feature.FeatureSpecExample"

      // failing test: feature("some feature") { scenario("some scenario")
      val testPath = "$specFqn/some feature -- some scenario"
      val url = "java:test://<kotest>$testPath</kotest>some scenario"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 3 is "some feature some scenario"
      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[3].tooltipText shouldBe "Run some feature some scenario"
   }

   fun testIconShowsPassedForPassedContainerTest() {
      myFixture.configureByFiles(
         "/featurespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.feature.FeatureSpecExample"

      // passing container: feature("some feature")
      val testPath = "$specFqn/some feature"
      val url = "java:suite://<kotest>$testPath</kotest>"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 2 is "some feature"
      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[2].tooltipText shouldBe "Run some feature"
   }

   fun testIconShowsFailedForFailedSpec() {
      myFixture.configureByFiles(
         "/featurespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.feature.FeatureSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[0].tooltipText shouldBe "Run FeatureSpecExample"
   }

   fun testIconShowsPassedForPassedSpec() {
      myFixture.configureByFiles(
         "/featurespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.feature.FeatureSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[0].tooltipText shouldBe "Run FeatureSpecExample"
   }
}

