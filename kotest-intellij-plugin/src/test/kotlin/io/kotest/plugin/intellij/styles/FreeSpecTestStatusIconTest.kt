package io.kotest.plugin.intellij.styles

import com.intellij.execution.TestStateStorage
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import java.nio.file.Paths
import java.util.Date

class FreeSpecTestStatusIconTest : LightJavaCodeInsightFixtureTestCase() {

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
         "/freespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.freespec.FreeSpecExample"

      // failing test: "some context" - { "more context" - { "as many as you want" - { "then a test"
      val testPath = "$specFqn/some context -- more context -- as many as you want -- then a test"
      val url = "java:test://<kotest>$testPath</kotest>then a test"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 4 is "then a test"
      gutters[4].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[4].tooltipText shouldBe "Run some context more context as many as you want then a test"
   }

   fun testIconShowsPassedForPassedTest() {
      myFixture.configureByFiles(
         "/freespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.freespec.FreeSpecExample"

      // passing test: "another context" - { "a test with config"
      val testPath = "$specFqn/another context"
      val url = "java:suite://<kotest>$testPath</kotest>"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 5 is "another context"
      gutters[5].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[5].tooltipText shouldBe "Run another context"
   }

   fun testIconShowsFailedForFailedSpec() {
      myFixture.configureByFiles(
         "/freespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.freespec.FreeSpecExample"

      // failing spec
      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 0 is the spec class
      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[0].tooltipText shouldBe "Run FreeSpecExample"
   }

   fun testIconShowsPassedForPassedSpec() {
      myFixture.configureByFiles(
         "/freespec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.freespec.FreeSpecExample"

      // passing spec
      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 0 is the spec class
      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[0].tooltipText shouldBe "Run FreeSpecExample"
   }
}

