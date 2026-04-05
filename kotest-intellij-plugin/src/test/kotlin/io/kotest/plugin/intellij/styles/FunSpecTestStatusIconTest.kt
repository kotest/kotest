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

class FunSpecTestStatusIconTest : LightJavaCodeInsightFixtureTestCase() {

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
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "io.kotest.samples.gradle.FunSpecExampleTest"

      // failing test: test("a test")
      val testPath = "$specFqn/a test"
      val url = "java:test://<kotest>$testPath</kotest>a test"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 1 is "a test"
      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[1].tooltipText shouldBe "Run a test"
   }

   fun testIconShowsPassedForPassedContainerTest() {
      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "io.kotest.samples.gradle.FunSpecExampleTest"

      // passing container: context("some context")
      val testPath = "$specFqn/some context"
      val url = "java:suite://<kotest>$testPath</kotest>"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 5 is "some context"
      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[5].tooltipText shouldBe "Run some context"
   }

   fun testIconShowsFailedForFailedSpec() {
      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "io.kotest.samples.gradle.FunSpecExampleTest"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[0].tooltipText shouldBe "Run FunSpecExampleTest"
   }

   fun testIconShowsPassedForPassedSpec() {
      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "io.kotest.samples.gradle.FunSpecExampleTest"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[0].tooltipText shouldBe "Run FunSpecExampleTest"
   }

   fun testIconShowsFailedForFailedTestWithoutKotestTags() {
      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "io.kotest.samples.gradle.FunSpecExampleTest"

      // failing test: test("a test") - without kotest tags
      val url = "java:test://$specFqn/a test"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 1 is "a test"
      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[1].tooltipText shouldBe "Run a test"
   }

   fun testIconShowsPassedForPassedContainerTestWithoutKotestTags() {
      myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "io.kotest.samples.gradle.FunSpecExampleTest"

      // passing container: context("some context") - without kotest tags
      val url = "java:suite://$specFqn/some context"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 5 is "some context"
      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[5].tooltipText shouldBe "Run some context"
   }
}

