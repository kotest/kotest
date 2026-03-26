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

class DescribeSpecTestStatusIconTest : LightJavaCodeInsightFixtureTestCase() {

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
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.describe.DescribeSpecExample"

      // failing test: describe("describe block") { it("it block")
      val testPath = "$specFqn/describe block -- it block"
      val url = "java:test://<kotest>$testPath</kotest>it block"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 2 is "it block"
      gutters[2].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[2].tooltipText shouldBe "Run describe block it block"
   }

   fun testIconShowsPassedForPassedContainerTest() {
      myFixture.configureByFiles(
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.describe.DescribeSpecExample"

      // passing container: describe("describe block")
      val testPath = "$specFqn/describe block"
      val url = "java:suite://<kotest>$testPath</kotest>"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 1 is "describe block"
      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[1].tooltipText shouldBe "Run describe block"
   }

   fun testIconShowsFailedForFailedSpec() {
      myFixture.configureByFiles(
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.describe.DescribeSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[0].tooltipText shouldBe "Run DescribeSpecExample"
   }

   fun testIconShowsPassedForPassedSpec() {
      myFixture.configureByFiles(
         "/describespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.describe.DescribeSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[0].tooltipText shouldBe "Run DescribeSpecExample"
   }
}

