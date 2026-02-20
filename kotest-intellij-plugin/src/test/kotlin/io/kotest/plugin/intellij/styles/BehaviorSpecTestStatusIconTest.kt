package io.kotest.plugin.intellij.styles

import com.intellij.execution.TestStateStorage
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths
import java.util.Date

class BehaviorSpecTestStatusIconTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   override fun setUp() {
      super.setUp()
      testMode = true
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
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.behavior.BehaviorSpecExample"

      // failing test: given("a given") { when("a when") { then("a test")
      val testPath = "$specFqn/a given -- a when -- a test"
      val url = "java:test://<kotest>$testPath</kotest>a test"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 3 is "a test"
      gutters[3].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[3].tooltipText shouldBe "Run a given a when a test"
   }

   fun testIconShowsPassedForPassedContainerTest() {
      myFixture.configureByFiles(
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.behavior.BehaviorSpecExample"

      // passing container: given("a given")
      val testPath = "$specFqn/a given"
      val url = "java:suite://<kotest>$testPath</kotest>"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 1 is "a given"
      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[1].tooltipText shouldBe "Run a given"
   }

   fun testIconShowsFailedForFailedSpec() {
      myFixture.configureByFiles(
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.behavior.BehaviorSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[0].tooltipText shouldBe "Run BehaviorSpecExample"
   }

   fun testIconShowsPassedForPassedSpec() {
      myFixture.configureByFiles(
         "/behaviorspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.behavior.BehaviorSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[0].tooltipText shouldBe "Run BehaviorSpecExample"
   }
}

