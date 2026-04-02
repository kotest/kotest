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

class AnnotationSpecTestStatusIconTest : LightJavaCodeInsightFixtureTestCase() {

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
         "/annotationspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.AnnotationSpecExample"

      // failing test: @Test fun test1()
      val testPath = "$specFqn/test1"
      val url = "java:test://<kotest>$testPath</kotest>test1"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 1 is "test1"
      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[1].tooltipText shouldBe "Run test1"
   }

   fun testIconShowsPassedForPassedTest() {
      myFixture.configureByFiles(
         "/annotationspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.AnnotationSpecExample"

      // passing test: @Test fun test1()
      val testPath = "$specFqn/test1"
      val url = "java:test://<kotest>$testPath</kotest>test1"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      // index 1 is "test1"
      gutters[1].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[1].tooltipText shouldBe "Run test1"
   }

   fun testIconShowsFailedForFailedSpec() {
      myFixture.configureByFiles(
         "/annotationspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.AnnotationSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.FAILED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Red2
      gutters[0].tooltipText shouldBe "Run AnnotationSpecExample"
   }

   fun testIconShowsPassedForPassedSpec() {
      myFixture.configureByFiles(
         "/annotationspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val storage = TestStateStorage.getInstance(project)
      val specFqn = "com.sksamuel.kotest.specs.AnnotationSpecExample"

      val url = "java:suite://$specFqn"
      storage.writeState(url, TestStateStorage.Record(TestStateInfo.Magnitude.PASSED_INDEX.value, Date(), 0, 0, "", "", ""))

      val gutters = myFixture.findAllGutters()

      gutters[0].icon shouldBe AllIcons.RunConfigurations.TestState.Green2
      gutters[0].tooltipText shouldBe "Run AnnotationSpecExample"
   }
}

